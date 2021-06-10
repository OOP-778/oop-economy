package com.oop.economy.util.number;

import com.google.common.primitives.Doubles;
import com.oop.economy.config.Configurations;
import com.oop.inteliframework.plugin.InteliPlatform;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberUtil {
  private static final Pattern charactersPattern = Pattern.compile("[a-zA-Z]+");
  private static final Pattern numbersPattern = Pattern.compile("[0-9]\\d{0,100}(\\.\\d{1,3})?");
  private static final BigDecimal BIG_DECIMAL_THOUSAND = BigDecimal.valueOf(1000);

  public static String formatBigDecimal(BigDecimal number) {
    String numberString = number.stripTrailingZeros().toBigInteger().toString();
    String s = numberString;
    int suffixIndex = 0;

    while (s.length() > 3) {
      s = s.substring(3);
      suffixIndex++;
    }

    if (suffixIndex == 0) {
      return numberString;
    }

    number = number.divide(BIG_DECIMAL_THOUSAND.pow(suffixIndex));

    // To round it to 2 digits.
    BigDecimal bigDecimal = number;
    bigDecimal = bigDecimal.setScale(2, BigDecimal.ROUND_FLOOR);

    // Add the number with the denomination to get the final value.
    return bigDecimal.stripTrailingZeros().toPlainString() + getSuffixes().get(suffixIndex);
  }

  private static List<String> extractMatches(String input, Pattern pattern) {
    List<String> matches = new LinkedList<>();
    Matcher matcher = pattern.matcher(input);
    while (matcher.find()) {
      matches.add(matcher.group());
    }

    return matches;
  }

  public static BigDecimal formattedToBigDecimal(String input) {
    input = input.replace(" ", "");
    List<String> numbers = extractMatches(input, numbersPattern);
    List<String> characters = extractMatches(input, charactersPattern);

    if (characters.size() == 0 && numbers.size() == 1) {
      return BigDecimal.valueOf(Doubles.tryParse(numbers.get(0)));
    }

    if (characters.size() != numbers.size()) {
      throw new IllegalStateException(
          "Failed to parse a number from " + input + " cause invalid format!");
    }

    BigDecimal value = BigDecimal.ZERO;
    for (int i = 0; i < characters.size(); i++) {
      BigDecimal number = BigDecimal.valueOf(Doubles.tryParse(numbers.get(i)));
      int suffixIndex = getSuffixes().indexOf(characters.get(i));
      if (suffixIndex == -1) {
        throw new IllegalStateException(
            "Failed to parse number from " + input + " cause invalid suffix: " + characters.get(i));
      }

      value =
          value.add(number.multiply(BIG_DECIMAL_THOUSAND.pow(suffixIndex))).stripTrailingZeros();
    }

    return value;
  }

  private static LinkedList<String> getSuffixes() {
    return InteliPlatform.getInstance()
        .safeModuleByClass(Configurations.class)
        .getMainConfig()
        .getNumberFormatterSuffixes()
        .get();
  }
}
