/**
 * predict4java: An SDP4 / SGP4 library for satellite orbit predictions
 * <p>
 * Copyright (C)  2004-2010  David A. B. Johnson, G4DPZ.
 * <p>
 * This class is a Java port of one of the core elements of
 * the Predict program, Copyright John A. Magliacane,
 * KD2BD 1991-2003: http://www.qsl.net/kd2bd/predict.html
 * <p>
 * Dr. T.S. Kelso is the author of the SGP4/SDP4 orbital models,
 * originally written in Fortran and Pascal, and released into the
 * public domain through his website (http://www.celestrak.com/).
 * Neoklis Kyriazis, 5B4AZ, later re-wrote Dr. Kelso's code in C,
 * and released it under the GNU GPL in 2002.
 * PREDICT's core is based on 5B4AZ's code translation efforts.
 * <p>
 * Author: David A. B. Johnson, G4DPZ <dave@g4dpz.me.uk>
 * <p>
 * Comments, questions and bugreports should be submitted via
 * http://sourceforge.net/projects/websat/
 * More details can be found at the project home page:
 * <p>
 * http://websat.sourceforge.net
 * <p>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, visit http://www.fsf.org/
 */
package uk.me.g4dpz.satellite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoUnit.*;
import static uk.me.g4dpz.satellite.PolePassed.*;

/**
 * Class which provides Pass Prediction.
 *
 * @author David A. B. Johnson, g4dpz
 */
public class PassPredictor {

  private static final double SPEED_OF_LIGHT = 2.99792458E8;
  private static final double TWOPI = Math.PI * 2.0;

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private boolean newTLE = true;

  private final TLE tle;
  private final GroundStationPosition location;
  private Satellite sat;
  private boolean windBackTime;
  private final double meanMotion;
  private int iterationCount;
  private Instant tca;

  /**
   * Constructor.
   *
   * @throws IllegalArgumentException bad argument passed in
   * @throws SatNotFoundException
   * @throws InvalidTleException
   */
  public PassPredictor(final TLE tle, final GroundStationPosition location) {

    if (tle == null) {
      throw new IllegalArgumentException("TLE has not been set");
    }

    if (location == null) {
      throw new IllegalArgumentException("GroundStationPosition has not been set");
    }

    this.tle = tle;
    this.location = location;

    newTLE = true;
    validateData();

    meanMotion = tle.getMeanmo();
  }

  /**
   * Gets the downlink frequency corrected for doppler.
   *
   * @param freq the original frequency in Hz
   * @return the doppler corrected frequency in Hz
   * @throws InvalidTleException  bad TLE passed in
   * @throws SatNotFoundException
   */
  public Long getDownlinkFreq(final Long freq, final Instant date) {
    validateData();
    // get the current position
    final SatPos satPos = getSatPos(date);
    final double rangeRate = satPos.getRangeRate();
    return (long) ((double) freq * (SPEED_OF_LIGHT - rangeRate * 1000.0) / SPEED_OF_LIGHT);
  }

  private SatPos getSatPos(final Instant time) {
    this.iterationCount++;
    return sat.getPosition(location, time);
  }

  /**
   * @param freq
   * @param date
   * @return the uplink frequency
   * @throws SatNotFoundException
   */
  public long getUplinkFreq(final long freq, final Instant date) {
    validateData();
    final SatPos satPos = getSatPos(date);
    final double rangeRate = satPos.getRangeRate();
    return (long) ((double) freq * (SPEED_OF_LIGHT + rangeRate * 1000.0) / SPEED_OF_LIGHT);
  }

  public SatPassTime nextSatPass(final Instant instant) {
    return nextSatPass(instant, false);
  }

  /**
   * Find the next satellite pass for a specific date
   *
   * @param instant  The date fo find the next pass for
   * @param windBack Whether to wind back 1/4 of an orbit
   * @return The satellite pass time
   * @throws InvalidTleException
   * @throws SatNotFoundException
   */
  public SatPassTime nextSatPass(Instant instant, final boolean windBack) {
    int aosAzimuth;
    int losAzimuth;
    double maxElevation = 0;
    double elevation;

    validateData();

    PolePassed polePassed = DEADSPOT_NONE;

    // get the current position

    // wind back time 1/4 of an orbit
    if (windBack) {
      instant = instant.plus((int) (-24.0 * 60.0 / meanMotion / 4.0), MINUTES);
    }

    SatPos satPos = getSatPos(instant);

    // test for the elevation being above the horizon
    if (isAboveHorizon(satPos)) {

      // move time forward in 30 second intervals until the sat goes below the horizon
      do {
        instant = instant.plus(60, SECONDS);
        satPos = getSatPos(instant);
      } while (isAboveHorizon(satPos));

      // move time forward 3/4 orbit
      instant = instant.plus(threeQuarterOrbitMinutes(), MINUTES);
    }

    // now find the next time it comes above the horizon
    do {
      instant = instant.plus(60, SECONDS);
      satPos = getSatPos(instant);
      final Instant now = instant;
      elevation = satPos.getElevation();
      if (elevation > maxElevation) {
        maxElevation = elevation;
        tca = now;
      }
    } while (isBelowHorizon(satPos));

    SatPos prevPos;

    // refine it to 5 seconds
    instant = instant.minus(60, SECONDS);
    do {
      instant = instant.plus(5, SECONDS);
      satPos = getSatPos(instant);
      final Instant now  = instant;
      elevation = satPos.getElevation();
      if (elevation > maxElevation) {
        maxElevation = elevation;
        tca = now;
      }
      prevPos = satPos;
    } while (isBelowHorizon(satPos));

    final Instant startDate = satPos.getTime();

    aosAzimuth = (int) ((satPos.getAzimuth() / (2.0 * Math.PI)) * 360.0);

    // now find when it goes below
    do {
      instant = instant.plus(30, SECONDS);
      satPos = getSatPos(instant);
      final Instant now  = instant;
      final PolePassed currPolePassed = getPolePassed(prevPos, satPos);
      if (currPolePassed != DEADSPOT_NONE) {
        polePassed = currPolePassed;
      }
      logger.debug("Current pole passed: {}", polePassed);
      elevation = satPos.getElevation();
      if (elevation > maxElevation) {
        maxElevation = elevation;
        tca = now;
      }
      prevPos = satPos;
    }
    while (isAboveHorizon(satPos));

    newTLE = true;
    validateData();

    // refine it to 5 seconds
    instant = instant.minus(30, SECONDS);
    do {
      instant = instant.plus(5, SECONDS);
      satPos = getSatPos(instant);
      final Instant now  = instant;
      elevation = satPos.getElevation();
      if (elevation > maxElevation) {
        maxElevation = elevation;
        tca = now;
      }
    } while (isAboveHorizon(satPos));

    final Instant endDate = satPos.getTime();
    losAzimuth = (int) ((satPos.getAzimuth() / (2.0 * Math.PI)) * 360.0);

    return new SatPassTime(startDate, endDate, tca, polePassed,
        aosAzimuth, losAzimuth, (maxElevation / (2.0 * Math.PI)) * 360.0);
  }

  /**
   * Gets a list of SatPassTime
   *
   * @param start Instant
   *              <p>
   *              newTLE = true; validateData();
   * @return List&lt;SatPassTime&gt;
   * @throws SatNotFoundException
   * @throws InvalidTleException
   */
  public List<SatPassTime> getPasses(final Instant start, final int hoursAhead, final boolean windBack) {

    this.iterationCount = 0;

    this.windBackTime = windBack;

    final List<SatPassTime> passes = new ArrayList<>();

    Instant trackStartDate = start;
    final Instant trackEndDate = start.plus(hoursAhead, HOURS);

    Instant lastAOS;

    int count = 0;

    do {
      if (count > 0) {
        this.windBackTime = false;
      }
      final SatPassTime pass = nextSatPass(trackStartDate, this.windBackTime);
      lastAOS = pass.getStartTime();
      passes.add(pass);
      trackStartDate = pass.getEndTime().plus(threeQuarterOrbitMinutes(), MINUTES);
      count++;
    } while (lastAOS.isBefore(trackEndDate));

    return passes;
  }

  /**
   * @return the iterationCount
   */
  public final int getIterationCount() {
    return iterationCount;
  }

  private void validateData() {
    if (newTLE) {
      sat = SatelliteFactory.createSatellite(tle);

      if (null == sat) {
        throw new SatNotFoundException("Satellite has not been created");
      }
      if (!sat.willBeSeen(location)) {
        throw new SatNotFoundException("Satellite will never appear above the horizon");
      }
      newTLE = false;
    }
  }

  /**
   * @return time in mS for 3/4 of an orbit
   */
  private int threeQuarterOrbitMinutes() {
    return (int) (24.0 * 60.0 / tle.getMeanmo() * 0.75);
  }

  private PolePassed getPolePassed(final SatPos prevPos, final SatPos satPos) {
    final double az1 = prevPos.getAzimuth() / TWOPI * 360.0;
    final double az2 = satPos.getAzimuth() / TWOPI * 360.0;

    if (az1 > az2) {
      // we may be moving from 350 or greater through north
      if (az1 > 350 && az2 < 10) {
        return NORTH;
      }
      // we may be moving from 190 or greater through south
      if (az1 > 180 && az2 < 180) {
        return SOUTH;
      }
    } else {
      // we may be moving from 10 or less through north
      if (az1 < 10 && az2 > 350) {
        return NORTH;
      }
      // we may be moving from 170 or more through south
      if (az1 < 180 && az2 > 180) {
        return SOUTH;
      }
    }

    return DEADSPOT_NONE;
  }

  /**
   * Calculates positions of satellite for a given point in time, time range and step increment.
   *
   * @param referenceDate
   * @param incrementSeconds
   * @param minutesBefore
   * @param minutesAfter
   * @return list of SatPos
   * @throws SatNotFoundException
   * @throws InvalidTleException
   */
  public List<SatPos> getPositions(final Instant referenceDate,
                                   final int incrementSeconds,
                                   final int minutesBefore,
                                   final int minutesAfter) {

    Instant trackDate = referenceDate.minus(minutesBefore, MINUTES);
    final Instant endDateDate = referenceDate.plus(minutesAfter, MINUTES);

    final List<SatPos> positions = new ArrayList<>();

    while (trackDate.isBefore(endDateDate)) {
      positions.add(getSatPos(trackDate));
      trackDate = trackDate.plusSeconds(incrementSeconds);
    }

    return positions;
  }

  private boolean isBelowHorizon(SatPos satPos) {
    return !isAboveHorizon(satPos);
  }

  private boolean isAboveHorizon(SatPos satPos) {
    return satPos.getElevation() > 0.0;
  }

}
