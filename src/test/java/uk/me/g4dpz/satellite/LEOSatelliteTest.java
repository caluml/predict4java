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

import org.junit.Test;

import java.time.Instant;

import static org.junit.Assert.*;

/**
 * @author David A. B. Johnson, g4dpz
 */
public class LEOSatelliteTest extends AbstractSatelliteTestBase {

  private static final String FORMAT_6_1F_6_1F = "%6.1f %6.1f";

  private static final String BASE_TIME = "2009-12-26T00:00:00Z";


  /**
   * Test method for
   * {@link LEOSatellite#LEOSatellite(TLE)}.
   */
  @Test
  public final void testLEOSatellite() {
    final Instant dateTime = Instant.parse("2009-04-17T06:57:32Z");
    final TLE tle = new TLE(LEO_TLE);
    assertFalse(tle.isDeepspace());
    final Satellite satellite = SatelliteFactory.createSatellite(tle);

    final SatPos satellitePosition = satellite.getPosition(GROUND_STATION, dateTime);

    assertEquals(3.2421950, satellitePosition.getAzimuth(), DELTA);
    assertEquals(0.1511580, satellitePosition.getElevation(), DELTA);
    assertEquals(6.2069835, satellitePosition.getLongitude(), DELTA);
    assertEquals(0.5648232, satellitePosition.getLatitude(), DELTA);
    assertEquals(818.1375014, satellitePosition.getAltitude(), DELTA);
    assertEquals(3.4337605, satellitePosition.getPhase(), DELTA);
    assertEquals(2506.0980852661323, satellitePosition.getRange(), DELTA);
    assertEquals(6.4832408, satellitePosition.getRangeRate(), DELTA);
    assertEquals(-0.9501914, satellitePosition.getTheta(), DELTA);
    assertEquals(-0.7307717, satellitePosition.getEclipseDepth(), DELTA);
    assertFalse(satellitePosition.isEclipsed());
    assertTrue(satellite.willBeSeen(GROUND_STATION));

    final double[][] rangeCircle = satellitePosition.getRangeCircle();
    assertEquals("  59.9  355.6", String.format(FORMAT_6_1F_6_1F, rangeCircle[0][0], rangeCircle[0][1]));
    assertEquals("  28.8  323.8", String.format(FORMAT_6_1F_6_1F, rangeCircle[89][0], rangeCircle[89][1]));
    assertEquals("   4.8  355.2", String.format(FORMAT_6_1F_6_1F, rangeCircle[179][0], rangeCircle[179][1]));
    assertEquals("  27.9   27.2", String.format(FORMAT_6_1F_6_1F, rangeCircle[269][0], rangeCircle[269][1]));
  }

  @Test
  public final void testWeatherSatellite() {
    final Instant dateTime = Instant.parse(BASE_TIME);
    final TLE tle = new TLE(WEATHER_TLE);
    assertFalse(tle.isDeepspace());
    final Satellite satellite = SatelliteFactory.createSatellite(tle);

    final SatPos satellitePosition = satellite.getPosition(GROUND_STATION, dateTime);

    assertEquals(0.0602822, satellitePosition.getAzimuth(), DELTA);
    assertEquals(-0.2617647, satellitePosition.getElevation(), DELTA);
    assertEquals(2.8305378, satellitePosition.getLongitude(), DELTA);
    assertEquals(1.4098576, satellitePosition.getLatitude(), DELTA);
    assertEquals(848.4319560, satellitePosition.getAltitude(), DELTA);
    assertEquals(4.5526109, satellitePosition.getPhase(), DELTA);
    assertEquals(5433.962087825947, satellitePosition.getRange(), DELTA);
    assertEquals(-3.0094317, satellitePosition.getRangeRate(), DELTA);
    assertEquals(-1.8011516, satellitePosition.getTheta(), DELTA);
    assertEquals(-0.2353420, satellitePosition.getEclipseDepth(), DELTA);
    assertFalse(satellitePosition.isEclipsed());
    assertTrue(satellite.willBeSeen(GROUND_STATION));
  }

  @Test
  public final void testIvoAlgorithm() {
    final Instant dateTime = Instant.parse(BASE_TIME);
    final TLE tle = new TLE(WEATHER_TLE);
    assertFalse(tle.isDeepspace());
    final Satellite satellite = SatelliteFactory.createSatellite(tle);
    satellite.calculateSatelliteVectors(dateTime);

    SatPos satellitePosition = satellite.calculateSatelliteGroundTrack();

    assertEquals(2.8305378, satellitePosition.getLongitude(), DELTA);
    assertEquals(1.4098576, satellitePosition.getLatitude(), DELTA);
    assertEquals(848.4319560, satellitePosition.getAltitude(), DELTA);
    assertEquals(4.5526109, satellitePosition.getPhase(), DELTA);
    assertEquals(-1.8011516, satellitePosition.getTheta(), DELTA);
    assertTrue(satellite.willBeSeen(GROUND_STATION));

    satellitePosition = satellite.calculateSatPosForGroundStation(GROUND_STATION);

    assertEquals(0.0602822, satellitePosition.getAzimuth(), DELTA);
    assertEquals(-0.2617647, satellitePosition.getElevation(), DELTA);
    assertEquals(5433.962087825947, satellitePosition.getRange(), DELTA);
    assertEquals(-3.0094317, satellitePosition.getRangeRate(), DELTA);
    assertEquals(-0.2353420, satellitePosition.getEclipseDepth(), DELTA);
    assertFalse(satellitePosition.isEclipsed());
  }

  @Test
  public final void testDeOrbitSatellite() {
    final Instant dateTime = Instant.parse(BASE_TIME);
    final TLE tle = new TLE(DE_ORBIT_TLE);
    assertFalse(tle.isDeepspace());
    final Satellite satellite = SatelliteFactory.createSatellite(tle);
    satellite.calculateSatelliteVectors(dateTime);

    final SatPos satellitePosition = satellite.calculateSatelliteGroundTrack();

    assertEquals(57.2854215, satellitePosition.getAltitude(), DELTA);

  }
}
