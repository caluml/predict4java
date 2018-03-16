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

import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author David A. B. Johnson, g4dpz
 */
public class DeepSpaceSatelliteTest extends AbstractSatelliteTestBase {

  @Test
  public final void testDeepSpaceSatellite() {
    final DateTime timeNow = new DateTime("2009-04-17T10:10:52Z");
    final TLE tle = new TLE(DEEP_SPACE_TLE);
    assertTrue(tle.isDeepspace());
    final Satellite satellite = SatelliteFactory.createSatellite(tle);

    final SatPos satellitePosition = satellite.getPosition(GROUND_STATION, timeNow.toDate());

    assertEquals(2.2579325, satellitePosition.getAzimuth(), DELTA);
    assertEquals(0.4144053, satellitePosition.getElevation(), DELTA);
    assertEquals(0.7091175, satellitePosition.getLongitude(), DELTA);
    assertEquals(0.0442970, satellitePosition.getLatitude(), DELTA);
    assertEquals(58847.2042542, satellitePosition.getAltitude(), DELTA);
    assertEquals(3.2039351, satellitePosition.getPhase(), DELTA);
    assertEquals(62390.2433539, satellitePosition.getRange(), DELTA);
    assertEquals(-0.2187132, satellitePosition.getRangeRate(), DELTA);
    assertEquals(0.6810134, satellitePosition.getTheta(), DELTA);
    assertEquals(-2.7759541, satellitePosition.getEclipseDepth(), DELTA);
    assertFalse(satellitePosition.isEclipsed());
    assertTrue(satellite.willBeSeen(GROUND_STATION));
  }

  @Test
  public final void testGeoSynchSatellite() {
    final DateTime dateTime = new DateTime("2009-12-26T00:00:00Z");
    final TLE tle = new TLE(GEOSYNC_TLE);
    assertTrue(tle.isDeepspace());
    final Satellite satellite = SatelliteFactory.createSatellite(tle);

    final SatPos satellitePosition = satellite.getPosition(GROUND_STATION, dateTime.toDate());

    assertTrue(tle.isDeepspace());
    assertEquals(5.7530820, satellitePosition.getAzimuth(), DELTA);
    assertEquals(-0.8368869, satellitePosition.getElevation(), DELTA);
    assertEquals(3.4946919, satellitePosition.getLongitude(), DELTA);
    assertEquals(-0.1440008, satellitePosition.getLatitude(), DELTA);
    assertEquals(36031.8182912, satellitePosition.getAltitude(), DELTA);
    assertEquals(0.5377382, satellitePosition.getPhase(), DELTA);
    assertEquals(46934.3153284, satellitePosition.getRange(), DELTA);
    assertEquals(0.0271561, satellitePosition.getRangeRate(), DELTA);
    assertEquals(-1.1369975, satellitePosition.getTheta(), DELTA);
    assertEquals(-2.5674344, satellitePosition.getEclipseDepth(), DELTA);
    assertFalse(satellitePosition.isEclipsed());
    assertTrue(satellite.willBeSeen(GROUND_STATION));
  }

  @Test
  public final void testMolniyaSatellite() {
    final DateTime dateTime = new DateTime("2009-12-26T00:00:00Z");
    final TLE tle = new TLE(MOLNIYA_TLE);
    final Satellite satellite = SatelliteFactory.createSatellite(tle);

    final SatPos satellitePosition = satellite.getPosition(GROUND_STATION, dateTime.toDate());

    assertTrue(tle.isDeepspace());
    assertEquals(6.2095948, satellitePosition.getAzimuth(), DELTA);
    assertEquals(0.0572862, satellitePosition.getElevation(), DELTA);
    assertEquals(3.2171857, satellitePosition.getLongitude(), DELTA);
    assertEquals(0.8635892, satellitePosition.getLatitude(), DELTA);
    assertEquals(35280.74696805362, satellitePosition.getAltitude(), DELTA);
    assertEquals(2.0315668, satellitePosition.getPhase(), DELTA);
    assertEquals(40814.87962007452, satellitePosition.getRange(), DELTA);
    assertEquals(0.9164450, satellitePosition.getRangeRate(), DELTA);
    assertEquals(-1.4145037, satellitePosition.getTheta(), DELTA);
    assertEquals(-1.7199331, satellitePosition.getEclipseDepth(), DELTA);
    assertFalse(satellitePosition.isEclipsed());
    assertTrue(satellite.willBeSeen(GROUND_STATION));
  }
}
