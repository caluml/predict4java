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

import static java.time.temporal.ChronoUnit.DAYS;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IlluminationTest extends AbstractSatelliteTestBase {

  @Test
  public void testCalculateSunVector() {
    final TLE tle = new TLE(LEO_TLE);
    assertFalse(tle.isDeepspace());
    final Satellite satellite = SatelliteFactory.createSatellite(tle);
    Instant instant = Instant.parse("2009-06-01T00:00:00Z");

    for (int day = 0; day < 30; day++) {
      final SatPos satPos = satellite.getPosition(GROUND_STATION, instant);

      switch (day) {
        case 4:
        case 9:
        case 14:
        case 19:
        case 24:
        case 29:
          assertTrue("Satellite should have been eclipsed on day " + day, satPos.isEclipsed());
          break;
        default:
          assertFalse("Satellite should not have been eclipsed on day " + day, satPos.isEclipsed());
          break;
      }
      instant = instant.plus(1, DAYS);
    }
  }

}
