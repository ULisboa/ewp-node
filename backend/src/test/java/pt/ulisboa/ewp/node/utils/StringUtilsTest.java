package pt.ulisboa.ewp.node.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class StringUtilsTest {

  @Test
  public void breakTextWithLineLengthLimit_GivenSmallText_ReturnOriginalText() {
    String text = "test\n123";
    int maximumLineLength = 10;

    String result = StringUtils.breakTextWithLineLengthLimit(text, "\n", maximumLineLength);

    assertThat(result).isEqualTo(text);
  }

  @Test
  public void breakTextWithLineLengthLimit_GivenSmallLineLength_ReturnTextBrokenIntoSeveralLines() {
    String text = "test\n123456789";
    int maximumLineLength = 3;

    String result = StringUtils.breakTextWithLineLengthLimit(text, "\n", maximumLineLength);

    assertThat(result).isEqualTo("tes\nt\n123\n456\n789");
  }

}