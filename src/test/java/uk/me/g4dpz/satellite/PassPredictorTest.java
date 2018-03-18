/**
 * predict4java: An SDP4 / SGP4 library for satellite orbit predictions
 * <p>
 * Copyright (C)  2004-2010  David A. B. Johnson, G4DPZ.
 * <p>
 * Author: David A. B. Johnson, G4DPZ <dave@g4dpz.me.uk>
 * <p>
 * Comments, questions and bug reports should be submitted via
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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.Instant;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static uk.me.g4dpz.satellite.PolePassed.*;

/**
 * @author David A. B. Johnson, g4dpz
 */
public class PassPredictorTest extends AbstractSatelliteTestBase {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test(timeout = 1000L)
  public void testIllegalArgumentsInConstructor() {
    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectMessage("TLE has not been set");

    new PassPredictor(null, null);
  }

  @Test(timeout = 1000L)
  public void testIllegalArgumentsInConstructor2() {
    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectMessage("GroundStationPosition has not been set");

    new PassPredictor(new TLE(LEO_TLE), null);
  }

  /**
   * Test method for {@link uk.me.g4dpz.satellite.PassPredictor#nextSatPass(Instant)}.
   */
  @Test(timeout = 1000L)
  public final void testNextSatPass() {
    final TLE tle = new TLE(LEO_TLE);
    assertFalse(tle.isDeepspace());
    final PassPredictor passPredictor = new PassPredictor(tle, GROUND_STATION);
    final Instant instant = Instant.parse("2009-01-05T00:00:00Z");

    SatPassTime passTime = passPredictor.nextSatPass(instant);

    assertEquals(Instant.parse("2009-01-05T04:28:10Z"), passTime.getStartTime());
    assertEquals(Instant.parse("2009-01-05T04:32:15Z"), passTime.getEndTime());
    assertEquals(Instant.parse("2009-01-05T04:30:10Z"), passTime.getTCA());
    assertEquals(DEADSPOT_NONE, passTime.getPolePassed());
    assertEquals(52, passTime.getAosAzimuth());
    assertEquals(84, passTime.getLosAzimuth());
    assertEquals("0.9", String.format("%3.1f", passTime.getMaxEl()));
    assertEquals(Long.valueOf(436802379L),
        passPredictor.getDownlinkFreq(436800000L, passTime.getStartTime()));
    assertEquals(Long.valueOf(145800719L).longValue(),
        passPredictor.getUplinkFreq(145800000L, passTime.getEndTime()));

    passTime = passPredictor.nextSatPass(passTime.getStartTime());
    assertEquals(Instant.parse("2009-01-05T06:04:00Z"), passTime.getStartTime());
    assertEquals(Instant.parse("2009-01-05T06:18:00Z"), passTime.getEndTime());
    assertEquals(DEADSPOT_NONE, passTime.getPolePassed());
    assertEquals(22, passTime.getAosAzimuth());
    assertEquals(158, passTime.getLosAzimuth());
    assertEquals(24.42, passTime.getMaxEl(), 0.02);

    passTime = passPredictor.nextSatPass(passTime.getStartTime());
    assertEquals(Instant.parse("2009-01-05T07:42:45Z"), passTime.getStartTime());
    assertEquals(Instant.parse("2009-01-05T07:57:50Z"), passTime.getEndTime());
    assertEquals(NORTH, passTime.getPolePassed());
    assertEquals(11, passTime.getAosAzimuth());
    assertEquals(207, passTime.getLosAzimuth());
    assertEquals("62.19", String.format("%5.2f", passTime.getMaxEl()));

    passTime = passPredictor.nextSatPass(passTime.getStartTime());
    assertEquals(Instant.parse("2009-01-05T09:22:05Z"), passTime.getStartTime());
    assertEquals(Instant.parse("2009-01-05T09:34:20Z"), passTime.getEndTime());
    assertEquals(NORTH, passTime.getPolePassed());
    assertEquals(4, passTime.getAosAzimuth());
    assertEquals(256, passTime.getLosAzimuth());
    assertEquals(14.3, passTime.getMaxEl(), 0.02);

    passTime = passPredictor.nextSatPass(passTime.getStartTime());
    assertEquals(Instant.parse("2009-01-05T11:02:05Z"), passTime.getStartTime());
    assertEquals(Instant.parse("2009-01-05T11:07:35Z"), passTime.getEndTime());
    assertEquals(DEADSPOT_NONE, passTime.getPolePassed());
    assertEquals(355, passTime.getAosAzimuth());
    assertEquals(312, passTime.getLosAzimuth());
    assertEquals(1.8, passTime.getMaxEl(), 0.05);
  }

  /**
   * Test method for {@link uk.me.g4dpz.satellite.PassPredictor#nextSatPass(Instant, boolean)}.
   */
  @Test(timeout = 1000L)
  public final void testNextSatPassWithWindBack() {
    final TLE tle = new TLE(LEO_TLE);
    assertFalse(tle.isDeepspace());
    final PassPredictor passPredictor = new PassPredictor(tle, GROUND_STATION);
    final Instant instant = Instant.parse("2009-01-05T04:30:00Z");

    final SatPassTime passTime = passPredictor.nextSatPass(instant, true);

    assertEquals(Instant.parse("2009-01-05T04:28:10Z"), passTime.getStartTime());
    assertEquals(Instant.parse("2009-01-05T04:32:15Z"), passTime.getEndTime());
    assertEquals(DEADSPOT_NONE, passTime.getPolePassed());
    assertEquals(52, passTime.getAosAzimuth());
    assertEquals(84, passTime.getLosAzimuth());
    assertEquals(0.9, passTime.getMaxEl(), 0.05);
    assertEquals(Long.valueOf(436802379L),
        passPredictor.getDownlinkFreq(436800000L, passTime.getStartTime()));
    assertEquals(Long.valueOf(145800719L).longValue(),
        passPredictor.getUplinkFreq(145800000L, passTime.getEndTime()));
  }

  @Test(timeout = 1000L)
  public void correctToStringResult() {
    final TLE tle = new TLE(LEO_TLE);
    assertFalse(tle.isDeepspace());
    final PassPredictor passPredictor = new PassPredictor(tle, GROUND_STATION);
    final Instant instant = Instant.parse("2009-01-05T04:30:00Z");

    final SatPassTime passTime = passPredictor.nextSatPass(instant, true);

    assertEquals("Date: January 5, 2009\n"
        + "Start Time: 4:28 AM\n"
        + "Duration:  4.1 min.\n"
        + "AOS Azimuth: 52 deg.\n"
        + "Max Elevation:  0.9 deg.\n"
        + "LOS Azimuth: 84 deg.", passTime.toString());
  }

  /**
   * test to determine if the antenna would track through a pole during a pass
   */
  @Test(timeout = 1000L)
  public final void poleIsPassed() {
    final TLE tle = new TLE(LEO_TLE);
    assertFalse(tle.isDeepspace());
    final PassPredictor passPredictor = new PassPredictor(tle, GROUND_STATION);
    Instant instant = Instant.parse("2009-01-05T07:00:00Z");
    boolean northFound = false;
    boolean southFound = false;

    for (int minute = 0; minute < 60 * 24 * 7; minute++) {
      final long startTime = instant.toEpochMilli();
      if (northFound && southFound) {
        break;
      }
      final SatPassTime passTime = passPredictor.nextSatPass(instant);
      final long endTime = passTime.getEndTime().toEpochMilli();
      final PolePassed polePassed = passTime.getPolePassed();
      if (polePassed != DEADSPOT_NONE) {
        if (!northFound && polePassed.equals(NORTH)) {
          assertEquals(NORTH, polePassed);
          assertEquals(Instant.parse("2009-01-05T07:42:45Z"), passTime.getStartTime());
          northFound = true;

          minute += (int) ((endTime - startTime) / 60000);
        } else if (!southFound && polePassed.equals(SOUTH)) {
          assertEquals(SOUTH, polePassed);
          assertEquals(Instant.parse("2009-01-06T07:03:20Z"), passTime.getStartTime());
          southFound = true;

          minute += (int) ((endTime - startTime) / 60000);
        }
      }

      instant = instant.plus(1, DAYS);
    }
  }

  @Test(timeout = 1000L)
  public void testGetPassList() {
    final TLE tle = new TLE(LEO_TLE);
    assertFalse(tle.isDeepspace());
    final PassPredictor passPredictor = new PassPredictor(tle, GROUND_STATION);
    final Instant instant = Instant.parse("2009-01-05T07:00:00Z");

    final List<SatPassTime> passed = passPredictor.getPasses(instant, 24, false);

    assertEquals(10, passed.size());
  }

  @Test(timeout = 1000L)
  public void testGetPassListWithWindBack() {
    final TLE tle = new TLE(LEO_TLE);
    assertFalse(tle.isDeepspace());
    final PassPredictor passPredictor = new PassPredictor(tle, GROUND_STATION);
    final Instant instant = Instant.parse("2009-01-05T07:00:00Z");

    final List<SatPassTime> passes = passPredictor.getPasses(instant, 24, true);

    assertEquals(10, passes.size());
    assertEquals(1039, passPredictor.getIterationCount());
  }

  @Test(timeout = 1000L)
  public void testGetSatelliteTrack() {
    final TLE tle = new TLE(LEO_TLE);
    assertFalse(tle.isDeepspace());
    final PassPredictor passPredictor = new PassPredictor(tle, GROUND_STATION);
    final Instant instant = Instant.parse("2009-01-05T07:00:00Z");
    final int incrementSeconds = 30;
    final int minutesBefore = 50;
    final int minutesAfter = 50;

    final List<SatPos> positions = passPredictor.getPositions(instant, incrementSeconds,
        minutesBefore, minutesAfter);

    assertEquals(200, positions.size());
  }
}