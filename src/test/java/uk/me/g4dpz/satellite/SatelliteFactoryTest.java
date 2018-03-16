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

import static org.junit.Assert.assertTrue;

/**
 * @author David A. B. Johnson, g4dpz
 */
public class SatelliteFactoryTest extends AbstractSatelliteTestBase {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void testCreateLEOSatellite() {
    final TLE tle = new TLE(LEO_TLE);

    final Satellite satellite = SatelliteFactory.createSatellite(tle);

    assertTrue(satellite instanceof LEOSatellite);
  }

  @Test
  public void testCreateDeepSpaceSatellite() {
    final TLE tle = new TLE(DEEP_SPACE_TLE);

    final Satellite satellite = SatelliteFactory.createSatellite(tle);

    assertTrue(satellite instanceof DeepSpaceSatellite);
  }

  @Test
  public void testNullTLE() {
    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectMessage("TLE was null");

    SatelliteFactory.createSatellite(null);
  }

  @Test
  public void testTLEWithWrongNumberOfRows() {
    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectMessage("TLE had 0 elements");

    final String[] theTLE = new String[0];
    final TLE tle = new TLE(theTLE);

    SatelliteFactory.createSatellite(tle);
  }
}
