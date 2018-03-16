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

import static org.junit.Assert.*;
import static uk.me.g4dpz.satellite.AbstractSatelliteTestBase.DELTA;

/**
 * @author David A. B. Johnson, g4dpz
 */
public final class GroundStationPositionTest {

  private static final double HEIGHT_AMSL = 3.0;
  private static final double LONGITUDE = 2.0;
  private static final double LATITUDE = 1.0;
  private static final double THETA = 4.0;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void testDefaultConstructorAndSetters() {
    final GroundStationPosition groundStationPosition = new GroundStationPosition();

    final int[] elevations = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1,
        2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2,
        3, 4, 5};
    groundStationPosition.setHorizonElevations(elevations);
    groundStationPosition.setTheta(4.0);

    final int[] oElevations = groundStationPosition.getHorizonElevations();

    assertEquals(elevations.length, oElevations.length);

    for (int i = 0; i < elevations.length; i++) {
      assertEquals(elevations[i], oElevations[i]);
    }

    assertTrue(Math.abs(THETA - groundStationPosition.getTheta()) < 0.000001);
  }

  @Test
  public void testConstructionUsingAttributes() {
    final GroundStationPosition groundStationPosition = new GroundStationPosition(LATITUDE, LONGITUDE, HEIGHT_AMSL);

    assertEquals(LATITUDE, groundStationPosition.getLatitude(), DELTA);
    assertEquals(LONGITUDE, groundStationPosition.getLongitude(), DELTA);
    assertEquals(HEIGHT_AMSL, groundStationPosition.getHeightAMSL(), DELTA);
  }

  @Test
  public void testSettingWrongNumberOfElevationsCausesException() {
    final GroundStationPosition groundStationPosition = new GroundStationPosition();
    final int[] elevations = new int[]{0, 1};

    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectMessage("Expected 36 Horizon Elevations, got: 2");

    groundStationPosition.setHorizonElevations(elevations);
  }
}
