package jp.co.sss.shop.util;

/**
 * 日本語文字の正規化を行うユーティリティクラス
 *
 * @author Jules
 */
public class JapaneseNormalizer {

    /**
     * ひらがなをカタカナに変換する
     *
     * @param text 変換対象の文字列
     * @return 変換後のカタカナ文字列。nullの場合は空文字を返す。
     */
    public static String toKatakana(String text) {
        if (text == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c >= '\u3041' && c <= '\u3096') {
                sb.append((char) (c + 0x60));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
