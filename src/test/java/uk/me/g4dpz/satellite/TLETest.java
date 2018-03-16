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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author David A. B. Johnson, g4dpz
 */
public final class TLETest extends AbstractSatelliteTestBase {

  private static final String TLELINE_3 = "2 28375  98.0821 101.6821 0084935  88.2048 272.8868 14.40599338194363";
  private static final String AO_51_NAME = "AO-51 [+]";

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void testTLEReadLEO() {
    final TLE tle = new TLE(LEO_TLE);
    checkData(tle);
  }

  @Test
  public void testCopyConstructor() {
    final TLE tle = new TLE(LEO_TLE);
    final TLE tleCopy = new TLE(tle);
    checkData(tleCopy);
  }

  @Test
  public void testNilStartTLE() {
    final TLE tle = new TLE(NIL_START_TLE, true);
    checkData(tle);
  }

  @Test
  public void testTLEReadDeepSpace() {
    final String[] theTLE = {
        "AO-40",
        "1 26609U 00072B   00326.22269097 -.00000581  00000-0  00000+0 0    29",
        "2 26609   6.4279 245.5626 7344055 179.5891 182.1915  2.03421959   104"};

    final TLE tle = new TLE(theTLE);

    assertTrue("Satellite should have been DeepSpace", tle.isDeepspace());
  }

  @Test
  public void testForNullDataInTLE() {
    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectMessage("TLE line[1] was null");

    final String[] theTLE = {AO_51_NAME, null, TLELINE_3};

    new TLE(theTLE);
  }

  @Test
  public void testForBlankDataInTLE() {
    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectMessage("TLE line[1] was zero length");

    final String[] theTLE = {AO_51_NAME, "", TLELINE_3};

    new TLE(theTLE);
  }

  @Test
  public void testForNoDataInTLE() {
    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectMessage("TLE had 0 elements");

    final String[] theTLE = new String[0];

    new TLE(theTLE);
  }

  @Test
  public void testLoadFromFile() throws IOException {
    InputStream fileIS = new FileInputStream("src/test/resources/LEO.txt");

    final List<TLE> tles = TLE.importSat(fileIS);

    assertEquals(1, tles.size());
    checkData(tles.get(0));
  }

  private void checkData(final TLE tle) {

    assertEquals(AO_51_NAME, tle.getName());
    assertEquals(AO_51_NAME, tle.toString());
    assertEquals(28375, tle.getCatnum());
    assertEquals(364, tle.getSetnum());
    assertEquals(9, tle.getYear());
    assertEquals(105.6639197, tle.getRefepoch(), DELTA);
    assertEquals(98.0551000, tle.getIncl(), DELTA);
    assertEquals(118.9086000, tle.getRaan(), DELTA);
    assertEquals(0.0084159, tle.getEccn(), DELTA);
    assertEquals(315.8041000, tle.getArgper(), DELTA);
    assertEquals(43.6444000, tle.getMeanan(), DELTA);
    assertEquals(14.4063845, tle.getMeanmo(), DELTA);
    assertEquals(0.0000, tle.getDrag(), DELTA);
    assertEquals(0.0000, tle.getNddot6(), DELTA);
    assertEquals(0.0000138, tle.getBstar(), DELTA);
    assertEquals(25195, tle.getOrbitnum());
    assertEquals(9105.6639197, tle.getEpoch(), DELTA);
    assertEquals(0.0000000, tle.getXndt2o(), DELTA);
    assertEquals(1.7113843, tle.getXincl(), DELTA);
    assertEquals(2.0753466, tle.getXnodeo(), DELTA);
    assertEquals(0.0084159, tle.getEo(), DELTA);
    assertEquals(5.5118213, tle.getOmegao(), DELTA);
    assertEquals(0.7617385, tle.getXmo(), DELTA);
    assertEquals(0.06285971070831925, tle.getXno(), DELTA);
    assertFalse(tle.isDeepspace());
  }
}
