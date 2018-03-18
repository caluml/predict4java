package uk.me.g4dpz.satellite;

import org.assertj.core.data.Offset;
import org.junit.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.me.g4dpz.satellite.AbstractSatellite.calcDaynum;

public class AbstractSatelliteTest {

  @Test
  public void Can_calculate_current_day_num() {
    Offset<Double> delta = Offset.offset(0.00000000001);

    assertThat(calcDaynum(Instant.parse("1983-01-06T12:34:45.013Z"))).isCloseTo(1102.5241320949074, delta);
    assertThat(calcDaynum(Instant.parse("1997-08-06T12:34:45.013Z"))).isCloseTo(6428.524132094907, delta);
    assertThat(calcDaynum(Instant.parse("2001-05-06T12:34:45.013Z"))).isCloseTo(7797.524132094907, delta);
    assertThat(calcDaynum(Instant.parse("2014-12-06T12:34:45.013Z"))).isCloseTo(12759.524132094906, delta);
    assertThat(calcDaynum(Instant.parse("2029-12-06T12:34:45.013Z"))).isCloseTo(18238.524132094906, delta);
    assertThat(calcDaynum(Instant.parse("2033-12-06T12:34:45.013Z"))).isCloseTo(19699.524132094906, delta);
  }
}
