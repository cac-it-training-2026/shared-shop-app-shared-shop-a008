package jp.co.sss.shop.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class JapaneseNormalizerTest {

    @Test
    public void testNormalize() {
        assertEquals("アイウエオ", JapaneseNormalizer.normalize("あいうえお"));
        assertEquals("カキクケコ", JapaneseNormalizer.normalize("カキクケコ"));
        assertEquals("サシスセソ", JapaneseNormalizer.normalize("さしすせそ"));
        assertEquals("パピプペポ", JapaneseNormalizer.normalize("ぱぴぷぺぽ"));
        assertEquals("ガギグゲゴ", JapaneseNormalizer.normalize("がぎぐげご"));
        assertEquals("漢字", JapaneseNormalizer.normalize("漢字"));
        assertEquals("ABC", JapaneseNormalizer.normalize("ABC"));
        assertEquals("123", JapaneseNormalizer.normalize("123"));
        assertNull(JapaneseNormalizer.normalize(null));
        assertEquals("", JapaneseNormalizer.normalize(""));
    }
}
