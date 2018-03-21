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

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SatPassTime {

  private Instant startTime;
  private Instant endTime;
  private Instant tca;
  private PolePassed polePassed;
  private int aos;
  private int los;
  private double maxEl;

  private static final String NEW_LINE = "\n";
  private static final String DEG_NL = " deg.\n";

  public SatPassTime(final Instant startTime, final Instant endTime, final Instant tca, final PolePassed polePassed,
                     final int aosAzimuth, final int losAzimuth,
                     final double maxEl) {
    this.startTime = startTime;
    this.endTime = endTime;
    this.polePassed = polePassed;
    this.aos = aosAzimuth;
    this.los = losAzimuth;
    this.maxEl = maxEl;
    this.tca = tca;
  }

  public final Instant getStartTime() {
    return startTime;
  }

  public final Instant getEndTime() {
    return endTime;
  }

  public final Instant getTCA() {
    return tca;
  }

  public final void setTCA(final Instant theTCA) {
    this.tca = theTCA;
  }

  public final PolePassed getPolePassed() {
    return polePassed;
  }

  public final int getAosAzimuth() {
    return aos;
  }

  public final int getLosAzimuth() {
    return los;
  }

  public final double getMaxEl() {
    return maxEl;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SatPassTime that = (SatPassTime) o;
    return aos == that.aos &&
        los == that.los &&
        Double.compare(that.maxEl, maxEl) == 0 &&
        Objects.equals(startTime, that.startTime) &&
        Objects.equals(endTime, that.endTime) &&
        Objects.equals(tca, that.tca) &&
        polePassed == that.polePassed;
  }

  @Override
  public int hashCode() {
    return Objects.hash(startTime, endTime, tca, polePassed, aos, los, maxEl);
  }

  /**
   * Returns a string representing the contents of the object.
   */
  @Override
  public String toString() {
    final double minutes = Duration.between(startTime, endTime).toMillis() / (double) TimeUnit.MINUTES.toMillis(1);

    final String formattedStartDate = startTime.atZone(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("MMMM d, yyyy"));
    final String formattedStartTime = startTime.atZone(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("h:mm a"));

    return "Date: " + formattedStartDate
        + NEW_LINE
        + "Start Time: "
        + formattedStartTime
        + NEW_LINE
        // "End Time: " + mTimeFormatter.format(endDate_time) + "\n" +
        + String.format("Duration: %4.1f min.\n", minutes)
        + "AOS Azimuth: " + aos + DEG_NL
        + String.format("Max Elevation: %4.1f deg.\n", maxEl)
        + "LOS Azimuth: " + los + " deg.";
  }
}
