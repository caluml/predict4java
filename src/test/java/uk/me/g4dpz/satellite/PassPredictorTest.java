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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * @author David A. B. Johnson, g4dpz
 */
public class PassPredictorTest extends AbstractSatelliteTestBase {

  private static final String DATE_2009_01_05T04_30_00Z = "2009-01-05T04:30:00Z";
  private static final String DATE_2009_01_05T04_32_15_0000 = "2009-01-05T04:32:15+0000";
  private static final String DATE_2009_01_05T04_28_10_0000 = "2009-01-05T04:28:10+0000";
  private static final String DATE_2009_01_05T07_00_00Z = "2009-01-05T07:00:00Z";
  private static final String NORTH = "north";
  private static final String STRING_PAIR = "%s, %s";
  private static final String NONE = "none";

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void testIllegalArgumentsInConstructor() throws SatNotFoundException, InvalidTleException {
    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectMessage("TLE has not been set");

    new PassPredictor(null, null);
  }

  @Test
  public void testIllegalArgumentsInConstructor2() throws SatNotFoundException, InvalidTleException {
    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectMessage("QTH has not been set");

    new PassPredictor(new TLE(LEO_TLE), null);
  }

  /**
   * Test method for {@link uk.me.g4dpz.satellite.PassPredictor#nextSatPass(java.util.Date)}.
   */
  @Test
  public final void testNextSatPass() throws SatNotFoundException, InvalidTleException {
    final TLE tle = new TLE(LEO_TLE);
    assertFalse(tle.isDeepspace());
    final PassPredictor passPredictor = new PassPredictor(tle, GROUND_STATION);
    final DateTime cal = new DateTime("2009-01-05T00:00:00Z");

    SatPassTime passTime = passPredictor.nextSatPass(cal.toDate());

    assertEquals(DATE_2009_01_05T04_28_10_0000, TZ_FORMAT.format(passTime.getStartTime()));
    assertEquals(DATE_2009_01_05T04_32_15_0000, TZ_FORMAT.format(passTime.getEndTime()));
    assertEquals("2009-01-05T04:30:10+0000", TZ_FORMAT.format(passTime.getTCA()));
    assertEquals(NONE, passTime.getPolePassed());
    assertEquals(52, passTime.getAosAzimuth());
    assertEquals(84, passTime.getLosAzimuth());
    assertEquals("0.9", String.format("%3.1f", passTime.getMaxEl()));
    assertEquals(Long.valueOf(436802379L),
        passPredictor.getDownlinkFreq(436800000L, passTime.getStartTime()));
    assertEquals(Long.valueOf(145800719L),
        passPredictor.getUplinkFreq(145800000L, passTime.getEndTime()));

    passTime = passPredictor.nextSatPass(passTime.getStartTime());
    assertEquals("2009-01-05T06:04:00+0000", TZ_FORMAT.format(passTime.getStartTime()));
    assertEquals("2009-01-05T06:18:00+0000", TZ_FORMAT.format(passTime.getEndTime()));
    assertEquals(NONE, passTime.getPolePassed());
    assertEquals(22, passTime.getAosAzimuth());
    assertEquals(158, passTime.getLosAzimuth());
    assertEquals(24.42, passTime.getMaxEl(), 0.02);

    passTime = passPredictor.nextSatPass(passTime.getStartTime());
    assertEquals("2009-01-05T07:42:45+0000", TZ_FORMAT.format(passTime.getStartTime()));
    assertEquals("2009-01-05T07:57:50+0000", TZ_FORMAT.format(passTime.getEndTime()));
    assertEquals(NORTH, passTime.getPolePassed());
    assertEquals(11, passTime.getAosAzimuth());
    assertEquals(207, passTime.getLosAzimuth());
    assertEquals("62.19", String.format("%5.2f", passTime.getMaxEl()));

    passTime = passPredictor.nextSatPass(passTime.getStartTime());
    assertEquals("2009-01-05T09:22:05+0000", TZ_FORMAT.format(passTime.getStartTime()));
    assertEquals("2009-01-05T09:34:20+0000", TZ_FORMAT.format(passTime.getEndTime()));
    assertEquals(NORTH, passTime.getPolePassed());
    assertEquals(4, passTime.getAosAzimuth());
    assertEquals(256, passTime.getLosAzimuth());
    assertEquals(14.3, passTime.getMaxEl(), 0.02);

    passTime = passPredictor.nextSatPass(passTime.getStartTime());
    assertEquals("2009-01-05T11:02:05+0000", TZ_FORMAT.format(passTime.getStartTime()));
    assertEquals("2009-01-05T11:07:35+0000", TZ_FORMAT.format(passTime.getEndTime()));
    assertEquals(NONE, passTime.getPolePassed());
    assertEquals(355, passTime.getAosAzimuth());
    assertEquals(312, passTime.getLosAzimuth());
    assertEquals(1.8, passTime.getMaxEl(), 0.05);
  }

  /**
   * Test method for {@link uk.me.g4dpz.satellite.PassPredictor#nextSatPass(java.util.Date, boolean)}.
   */
  @Test
  public final void testNextSatPassWithWindBack() throws SatNotFoundException, InvalidTleException {
    final TLE tle = new TLE(LEO_TLE);
    assertFalse(tle.isDeepspace());
    final PassPredictor passPredictor = new PassPredictor(tle, GROUND_STATION);
    final DateTime cal = new DateTime(DATE_2009_01_05T04_30_00Z);

    final SatPassTime passTime = passPredictor.nextSatPass(cal.toDate(), true);

    assertEquals(DATE_2009_01_05T04_28_10_0000, TZ_FORMAT.format(passTime.getStartTime()));
    assertEquals(DATE_2009_01_05T04_32_15_0000, TZ_FORMAT.format(passTime.getEndTime()));
    assertEquals(NONE, passTime.getPolePassed());
    assertEquals(52, passTime.getAosAzimuth());
    assertEquals(84, passTime.getLosAzimuth());
    assertEquals(0.9, passTime.getMaxEl(), 0.05);
    assertEquals(Long.valueOf(436802379L),
        passPredictor.getDownlinkFreq(436800000L, passTime.getStartTime()));
    assertEquals(Long.valueOf(145800719L),
        passPredictor.getUplinkFreq(145800000L, passTime.getEndTime()));
  }

  @Test
  public void correctToStringResult() throws SatNotFoundException, InvalidTleException {
    final TLE tle = new TLE(LEO_TLE);
    assertFalse(tle.isDeepspace());
    final PassPredictor passPredictor = new PassPredictor(tle, GROUND_STATION);
    final DateTime cal = new DateTime(DATE_2009_01_05T04_30_00Z);

    final SatPassTime passTime = passPredictor.nextSatPass(cal.toDate(), true);

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
  @Test
  public final void poleIsPassed() throws SatNotFoundException, InvalidTleException {
    final TLE tle = new TLE(LEO_TLE);
    assertFalse(tle.isDeepspace());
    final PassPredictor passPredictor = new PassPredictor(tle, GROUND_STATION);
    DateTime cal = new DateTime(DATE_2009_01_05T07_00_00Z);
    boolean northFound = false;
    boolean southFound = false;

    for (int minute = 0; minute < 60 * 24 * 7; minute++) {
      final long startTime = cal.toDate().getTime();
      if (northFound && southFound) {
        break;
      }
      final SatPassTime passTime = passPredictor.nextSatPass(cal.toDate());
      final long endTime = passTime.getEndTime().getTime();
      final String polePassed = passTime.getPolePassed();
      if (!polePassed.equals(NONE)) {
        if (!northFound && polePassed.equals(NORTH)) {
          assertEquals("2009-01-05T07:42:45+0000, north", String.format(STRING_PAIR,
              TZ_FORMAT.format(passTime.getStartTime()), polePassed));
          northFound = true;

          minute += (int) ((endTime - startTime) / 60000);
        } else if (!southFound && polePassed.equals("south")) {
          assertEquals("2009-01-06T07:03:20+0000, south", String.format(STRING_PAIR,
              TZ_FORMAT.format(passTime.getStartTime()), polePassed));
          southFound = true;

          minute += (int) ((endTime - startTime) / 60000);
        }
      }

      cal = cal.plusMinutes(minute);
    }
  }

  @Test
  public void testGetPassList() throws InvalidTleException, SatNotFoundException {
    final TLE tle = new TLE(LEO_TLE);
    assertFalse(tle.isDeepspace());
    final PassPredictor passPredictor = new PassPredictor(tle, GROUND_STATION);
    final DateTime start = new DateTime(DATE_2009_01_05T07_00_00Z);

    final List<SatPassTime> passed = passPredictor.getPasses(start.toDate(), 24, false);

    assertEquals(10, passed.size());
  }

  @Test
  public void testGetPassListWithWindBack() throws InvalidTleException, SatNotFoundException {
    final TLE tle = new TLE(LEO_TLE);
    assertFalse(tle.isDeepspace());
    final PassPredictor passPredictor = new PassPredictor(tle, GROUND_STATION);
    final DateTime start = new DateTime(DATE_2009_01_05T07_00_00Z);

    final List<SatPassTime> passes = passPredictor.getPasses(start.toDate(), 24, true);

    assertEquals(10, passes.size());
    assertEquals(1039, passPredictor.getIterationCount());
  }

  @Test
  public void testGetSatelliteTrack() throws Exception {
    final TLE tle = new TLE(LEO_TLE);
    assertFalse(tle.isDeepspace());
    final PassPredictor passPredictor = new PassPredictor(tle, GROUND_STATION);
    final DateTime referenceDate = new DateTime(DATE_2009_01_05T07_00_00Z);
    final int incrementSeconds = 30;
    final int minutesBefore = 50;
    final int minutesAfter = 50;

    final List<SatPos> positions = passPredictor.getPositions(referenceDate.toDate(), incrementSeconds,
        minutesBefore, minutesAfter);

    assertEquals(200, positions.size());
  }

}
