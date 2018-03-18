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

import java.text.NumberFormat;
import java.time.Instant;
import java.util.Date;

/**
 * @author g4dpz
 */
public class SatPos {

  private static final String NL = "\n";
  private static final String DEG_CR = " deg.\n";

  /* WGS 84 Earth radius km */
  private static final double EARTH_RADIUS = 6.378137E3;
  private static final double R0 = 6378.16;

  // the internal representation will be in radians
  private double azimuth;
  private double elevation;
  private double latitude;
  private double longitude;

  private Instant time;
  private double range;
  private double rangeRate;
  private double phase;
  private double altitude;
  private double theta;

  private double eclipseDepth;
  private boolean eclipsed;

  private boolean aboveHorizon;


  public double getAzimuth() {
    return azimuth;
  }

  public double getElevation() {
    return elevation;
  }

  public Date getTime() {
    return new Date(time.toEpochMilli());
  }

  public final double getRange() {
    return range;
  }

  public final void setRange(final double range) {
    this.range = range;
  }

  public final double getRangeRate() {
    return rangeRate;
  }

  public final void setRangeRate(final double rangeRate) {
    this.rangeRate = rangeRate;
  }

  public final double getPhase() {
    return phase;
  }

  public final void setPhase(final double phase) {
    this.phase = phase;
  }

  public final double getLatitude() {
    return latitude;
  }

  public final void setLatitude(final double latitude) {
    this.latitude = latitude;
  }

  public final double getLongitude() {
    return longitude;
  }

  public final void setLongitude(final double longitude) {
    this.longitude = longitude;
  }

  public final double getAltitude() {
    return altitude;
  }

  public final void setAltitude(final double altitude) {
    this.altitude = altitude;
  }

  public final double getTheta() {
    return theta;
  }

  public final void setTheta(final double theta) {
    this.theta = theta;
  }

  public final void setAzimuth(final double azimuth) {
    this.azimuth = azimuth;
  }

  public final void setElevation(final double elevation) {
    this.elevation = elevation;
  }

  public final void setTime(final Instant time) {
    this.time = time;
  }

  public final boolean isAboveHorizon() {
    return aboveHorizon;
  }

  public final void setAboveHorizon(final boolean aboveHorizon) {
    this.aboveHorizon = aboveHorizon;
  }

  protected final double getEclipseDepth() {
    return eclipseDepth;
  }

  protected final void setEclipseDepth(final double eclipseDepth) {
    this.eclipseDepth = eclipseDepth;
  }

  protected final boolean isEclipsed() {
    return eclipsed;
  }

  protected final void setEclipsed(final boolean eclipsed) {
    this.eclipsed = eclipsed;
  }

  @Override
  public String toString() {
    return "Azimuth:    " + azimuth / (Math.PI * 2.0) * 360 + DEG_CR
        + "Elevation:  " + elevation / (Math.PI * 2.0) * 360 + DEG_CR
        + "Latitude:   " + latitude / (Math.PI * 2.0) * 360 + DEG_CR
        + "Longitude:  " + longitude / (Math.PI * 2.0) * 360 + DEG_CR

        + "Date:       " + time + NL
        + "Range:        " + range + " km.\n"
        + "Range rate:   " + rangeRate + " m/S.\n"
        + "Phase:        " + phase + " /(256)\n"
        + "Altitude:     " + altitude + " km\n"
        + "Theta:        " + theta + " rad/sec\n"
        + "Eclipsed:     " + eclipsed + NL
        + "Eclipse depth:" + eclipseDepth + " radians\n";
  }

  public String toShortString() {
    String returnString = "";

    final NumberFormat numberFormat = NumberFormat.getNumberInstance();

    numberFormat.setMaximumFractionDigits(0);
    returnString = returnString
        + "Elevation: " + numberFormat.format(elevation / (Math.PI * 2.0) * 360) + DEG_CR
        + "Azimuth: " + numberFormat.format(azimuth / (Math.PI * 2.0) * 360) + DEG_CR;

    numberFormat.setMaximumFractionDigits(2);
    returnString = returnString
        + "Latitude: " + numberFormat.format(latitude / (Math.PI * 2.0) * 360) + DEG_CR
        + "Longitude: " + numberFormat.format(longitude / (Math.PI * 2.0) * 360) + DEG_CR;

    numberFormat.setMaximumFractionDigits(0);
    returnString = returnString
        + "Range: " + numberFormat.format(range) + " Km";

    return returnString;

  }

  public void copy(final SatPos other) {

    aboveHorizon = other.aboveHorizon;
    altitude = other.altitude;
    azimuth = other.azimuth;
    elevation = other.elevation;
    latitude = other.latitude;
    longitude = other.longitude;
    phase = other.phase;
    range = other.range;
    rangeRate = other.rangeRate;
    theta = other.theta;
    time = other.time;
    eclipseDepth = other.eclipseDepth;
    eclipsed = other.eclipsed;
  }

  /*
   * Gets the range circle as an array of integers representing pairs of latitude and longitude.
   */
  public final double[][] getRangeCircle() {

    return calculateRangeCirclePoints(this);

  }

  /**
   * Calculates the footprint range circle.
   *
   * @param pos
   * @return double array of lat/long
   */
  private static double[][] calculateRangeCirclePoints(final SatPos pos) {

    final int dia = (int) (12756.33 * Math.acos(EARTH_RADIUS / (EARTH_RADIUS + pos.altitude)));

    final double latitude = pos.latitude;
    final double longitude = pos.longitude;
    final double beta = (0.5 * dia) / R0;
    final double[][] result = new double[360][2];

    for (int azi = 0; azi < 360; azi++) {
      final double azimuth = (double) (azi / 360.0) * 2.0 * Math.PI;
      double rangelat = Math.asin(Math.sin(latitude) * Math.cos(beta) + Math.cos(azimuth) * Math.sin(beta)
          * Math.cos(latitude));
      final double num = Math.cos(beta) - (Math.sin(latitude) * Math.sin(rangelat));
      final double den = Math.cos(latitude) * Math.cos(rangelat);
      double rangelong;

      if (azi == 0 && (beta > ((Math.PI / 2.0) - latitude))) {
        rangelong = longitude + Math.PI;
      } else if (azi == 180 && (beta > ((Math.PI / 2.0) - latitude))) {
        rangelong = longitude + Math.PI;
      } else if (Math.abs(num / den) > 1.0) {
        rangelong = longitude;
      } else {
        if ((180 - azi) >= 0) {
          rangelong = longitude - Math.acos(num / den);
        } else {
          rangelong = longitude + Math.acos(num / den);
        }
      }

      while (rangelong < 0.0) {
        rangelong += Math.PI * 2.0;
      }

      while (rangelong > Math.PI * 2.0) {
        rangelong -= Math.PI * 2.0;
      }

      rangelat = (rangelat / (2.0 * Math.PI)) * 360.0;
      rangelong = (rangelong / (2.0 * Math.PI)) * 360.0;

//            if (rangelong < 180.0) {
//                rangelong = -rangelong;
//            }
//            else if (rangelong > 180.0) {
//                rangelong = 360.0 - rangelong;
//            }
//
//            if (rangelat < 90.0) {
//                rangelat = -rangelat;
//            }
//            else if (rangelat > 90.0) {
//                rangelat = 180.0 - rangelat;
//            }

      result[azi][0] = rangelat;
      result[azi][1] = rangelong;

    }

    return result;
  }

}
