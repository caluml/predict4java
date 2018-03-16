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

import static org.junit.Assert.assertEquals;


/**
 * @author David A. B. Johnson, g4dpz
 */
public final class SatPosTest {

  private static final String FORMAT_4F = "%4.0f %4.0f";

  @Test
  public void footprintCalculatedCorrectly() {
    final SatPos pos = new SatPos();
    pos.setLatitude(0);
    pos.setLongitude(0);
    pos.setAltitude(1000);
    double[][] rangeCircle = pos.getRangeCircle();
    assertEquals("  30    0", String.format(FORMAT_4F, rangeCircle[0][0], rangeCircle[0][1]));
    assertEquals("   1  330", String.format(FORMAT_4F, rangeCircle[89][0], rangeCircle[89][1]));
    assertEquals(" -30  359", String.format(FORMAT_4F, rangeCircle[179][0], rangeCircle[179][1]));
    assertEquals("  -1   30", String.format(FORMAT_4F, rangeCircle[269][0], rangeCircle[269][1]));


    pos.setLatitude(10.0 / 360.0 * 2.0 * Math.PI);
    pos.setLongitude(10.0 / 360.0 * 2.0 * Math.PI);
    pos.setAltitude(1000);
    rangeCircle = pos.getRangeCircle();
    assertEquals("  40   10", String.format(FORMAT_4F, rangeCircle[0][0], rangeCircle[0][1]));
    assertEquals("   9  339", String.format(FORMAT_4F, rangeCircle[89][0], rangeCircle[89][1]));
    assertEquals(" -20    9", String.format(FORMAT_4F, rangeCircle[179][0], rangeCircle[179][1]));
    assertEquals("   8   41", String.format(FORMAT_4F, rangeCircle[269][0], rangeCircle[269][1]));
  }
}
