package jp.co.lhe.lhememberapp.utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * <p>文字列を操作するためのクラスです。</p>
 * @author OSCA
 */
public class StringUtils {

	/**
	 * <p>指定した文字列がNULLもしくは空文字("")かを検証します。</p>
	 * @param str 検証対象文字列
	 * @return 指定した文字列がNULLもしくは空文字("")である場合にtrueを返却します。
	 */
	public static boolean isNullOrEmpty(final String str) {
		return (str == null || str.isEmpty());
	}

	/**
	 * <p>指定した文字列が空文字("")である場合にNULLに変換して返却します。</p>
	 * @param str 変換対象文字列
	 * @return 指定した文字列がNULLもしくは空文字("")である場合にNULLを返却します。 それ以外の場合は、指定した文字列をそのまま返却します。
	 */
	public static String convertEmptyToNull(final String str) {
		return (isNullOrEmpty(str)) ? null : str;
	}

	/**
	 * <p>指定した文字列がNULLである場合に"0"に変換して返却します。</p>
	 * @param str 変換対象文字列
	 * @return 指定した文字列がNULLである場合に"0"を返却します。 それ以外の場合は、指定した文字列をそのまま返却します。
	 */
	public static String convertNullToZero(final String str) {
		return (str == null) ? "0" : str ;
	}



	/**
	 * <p>指定した文字列を指定したセパレータを元に分解しリストに変換して返却します。</p>
	 * @param str 変換対象文字列
	 * @param separator セパレータ
	 * @return 変換されたリスト
	 */
	public static List<String> convertList(final String str, final String separator) {
		if (str == null || separator == null) {
			return null;
		}

		String[] strs = str.split(separator);
		return new ArrayList<String>(Arrays.asList(strs));
	}

	/**
	 * <p>指定した文字列の先頭に指定文字を文字列全体で指定文字数になる文字列を生成して返却します。</p>
	 * @param str 変換対象文字列
	 * @param length 変換後文字数
	 * @param padChar 先頭に埋める文字
	 * @return 変換後文字列
	 */
	public static String lpad(final String str, final int length, final char padChar) {
		// 例外処理
		if (str == null) {
			throw new IllegalArgumentException("Method argument 'str' is null.");
		}

		// 指定された文字列が指定文字数を超過
		if (str.length() >= length) {
			return str;
		}
		// 文字列の先頭パディング
		StringBuffer ret = new StringBuffer();
		for (int i = str.length(); i < length; i++) {
			ret.append(padChar);
		}
		ret.append(str);

		return ret.toString();
	}

	/**
	 * <p>指定した文字列の後ろに指定文字を文字列全体で指定文字数になる文字列を生成して返却します。</p>
	 * @param str 変換対象文字列
	 * @param length 変換後文字数
	 * @param padChar 後ろに埋める文字
	 * @return 変換後文字列
	 */
	public static String rpad(final String str, final int length, final char padChar) {
		if( str == null ) throw new IllegalArgumentException("Method argument 'str' is null.") ;

		// 指定された文字列が指定文字数を超過
		if (str.length() >= length) return str ;

		// 文字列の先頭パディング
		StringBuffer ret    = new StringBuffer();
		StringBuffer strTmp = new StringBuffer();
		for (int i = str.length(); i < length; i++) {
			ret.append(padChar);
		}

		strTmp.append(str);
		strTmp.append(ret);

		return strTmp.toString();
	}

	/**
	 * <p>指定した文字列の先頭から指定文字数までの文字列を生成して返却する。</p>
	 * @param str 変換対象文字列
	 * @param length 変換後文字数
	 * @return 変換後文字列
	 */
	public static String left(final String str, final int length) {
		// 例外処理
		if (str == null) {
			throw new IllegalArgumentException("Method argument 'str' is null.");
		}
		// 文字数不足
		if (str.length() <= length) {
			return str;
		}

		return str.substring(0, length);
	}

	/**
	 * <p>指定した文字列の後方から指定文字数までの文字列を生成して返却する。</p>
	 * @param str 変換対象文字列
	 * @param length 変換後文字数
	 * @return 変換後文字列
	 */
	public static String right(final String str, final int length) {
		// 例外処理
		if (str == null) {
			throw new IllegalArgumentException("Method argument 'str' is null.");
		}
		// 文字数不足
		if (str.length() <= length) {
			return str;
		}

		return str.substring(str.length() - length, str.length());
	}

	/**
	 * <p>指定した電話番号を連結して返却する。</p>
	 * @param telU 電話番号の上桁
	 * @param telM 電話番号の中桁
	 * @param telL 電話番号の下桁
	 * @return 連結後文字列
	 */
	public static String concatTel(final String telU, final String telM, final String telL) {
		// 例外処理
		if (telU == null) {
			throw new IllegalArgumentException("Method argument 'telU' is null.");
		}
		if (telM == null) {
			throw new IllegalArgumentException("Method argument 'telM' is null.");
		}
		if (telL == null) {
			throw new IllegalArgumentException("Method argument 'telL' is null.");
		}
		// すべてEmpty
		if (telU.isEmpty() && telM.isEmpty() && telL.isEmpty()) {
			return "";
		}
		return telU + "-" + telM + "-" + telL;
	}

	/**
	 * <p>指定したメールアドレスを連結して返却する。</p>
	 * @param mailLocal メールアドレスのローカル部
	 * @param mailDomain メールアドレスのドメイン部
	 * @return 連結後文字列
	 */
	public static String concatMail(final String mailLocal, final String mailDomain) {
		// 例外処理
		if (mailLocal == null) {
			throw new IllegalArgumentException("Method argument 'mailLocal' is null.");
		}
		if (mailDomain == null) {
			throw new IllegalArgumentException("Method argument 'mailDomain' is null.");
		}
		// すべてEmpty
		if (mailLocal.isEmpty() && mailDomain.isEmpty()) {
			return "";
		}
		return mailLocal + "@" + mailDomain;
	}

	/**
	 * <p>指定した氏名を連結して返却する。</p>
	 * @param lastName 氏名（姓）
	 * @param firstName 氏名（名）
	 * @return 連結後文字列
	 */
	public static String concatName(final String lastName, final String firstName) {
		// 例外処理
		if (lastName == null) {
			throw new IllegalArgumentException("Method argument 'lastName' is null.");
		}
		if (firstName == null) {
			throw new IllegalArgumentException("Method argument 'firstName' is null.");
		}
		// すべてEmpty
		if (lastName.isEmpty() && firstName.isEmpty()) {
			return "";
		}
		return lastName + "　" + firstName;
	}

	/**
	 * <p>指定した氏名カナを連結して返却する。</p>
	 * @param lastNameKana 氏名カナ（姓）
	 * @param firstNameKana 氏名カナ（名）
	 * @return 連結後文字列
	 */
	public static String concatNameKana(final String lastNameKana, final String firstNameKana) {
		// 例外処理
		if (lastNameKana == null) {
			throw new IllegalArgumentException("Method argument 'lastNameKana' is null.");
		}
		if (firstNameKana == null) {
			throw new IllegalArgumentException("Method argument 'firstNameKana' is null.");
		}
		// すべてEmpty
		if (lastNameKana.isEmpty() && firstNameKana.isEmpty()) {
			return "";
		}
		return lastNameKana + "　" + firstNameKana;
	}

	/**
	 * <p>指定した郵便番号をハイフン付きで返却する。</p>
	 * @param zip 郵便番号
	 * @return ハイフン付きの郵便番号
	 */
	public static String formatZip(final String zip) {
		// 例外処理
		if (zip == null) {
			throw new IllegalArgumentException("Method argument 'zip' is null.");
		}

		if (zip.isEmpty()) {
			return "";
		}

		if (zip.length() < 4) {
			return zip;
		}

		return zip.substring(0, 3) + "-" + zip.substring(3);
	}

	/**
	 * <p>指定された電話番号を分割する。</p>
	 * @param target 対象文字列
	 * @return 指定された電話番号を分割して返却する。
	 */
	public static List<String> divideTelephoneNo(final String target) {
		List<String> ret = new ArrayList<>();

		if (target != null) {
			ret = Arrays.asList(target.split("-"));
		}
		return ret;
	}

	/**
	 * <p>指定されたメールアドレスを分割する。</p>
	 * @param target 対象文字列
	 * @return 指定されたメールアドレスを分割して返却する。
	 */
	public static List<String> divideMail(final String target) {
		List<String> ret = new ArrayList<>();

		if (target != null) {
			ret = Arrays.asList(target.split("@"));
		}

		return ret;
	}

	/**
	 * <p>指定されたメールアドレスからローカル部を取得する。</p>
	 * @param mail メールアドレス
	 * @return メールアドレスのローカル部
	 */
	public static String getLocalFromMailAddress(final String mail) {

		if (mail == null) {
			return mail;
		}

		if (mail.indexOf("@") < 0) {
			return mail;
		}

		return mail.substring(0,mail.indexOf("@"));
	}

	/**
	 * <p>指定されたメールアドレスからドメイン部を取得する。</p>
	 * @param mail メールアドレス
	 * @return メールアドレスのドメイン部
	 */
	public static String getDomainFromMailAddress(final String mail) {

		if (mail == null) {
			return mail;
		}

		if (mail.indexOf("@") < 0) {
			return "";
		}

		return mail.substring(mail.indexOf("@")+1, mail.length());
	}

	/**
	 * <p>指定された氏名を分割する。</p>
	 * @param target 対象文字列
	 * @return 指定された氏名を分割して返却する。
	 */
	public static List<String> divideName(final String target) {
		List<String> ret = new ArrayList<>();

		if (target != null) {
			ret = Arrays.asList(target.split("　", 2));
		}

		return ret;
	}

	/**
	 * <p>指定された氏名カナを分割する。</p>
	 * @param target 対象文字列
	 * @return 指定された氏名カナを分割して返却する。
	 */
	public static List<String> divideNameKana(final String target) {
		List<String> ret = new ArrayList<>();

		if (target != null) {
			ret = Arrays.asList(target.split(" |　", 2));
		}

		return ret;
	}

	/**
	 * <p>指定した区切り文字で区切った文字列を返却する。</p>
	 * @param target 編集前の文字列
	 * @param separator 区切り文字
	 * @return 指定した区切り文字で区切った文字列
	 */
	public static String addSeparator(final String target, final String separator) {

		if (target == null) {
			return target;
		}

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < target.length(); i++) {
			if (i > 0) {
				sb.append(separator);
			}
			sb.append(target.substring(i, i + 1));
		}

		return sb.toString();
	}

	/**
	 * <p>引数の文字列からHTMLタグを除去した文字列を返却する。</p>
	 * @param target
	 * @return HTMLタグを除去した文字列
	 */
	public static String removeHTMLTags(String target) {
	    if( target == null ) throw new IllegalArgumentException("Method argument 'target' is null.") ;
	    return target.replaceAll("<(\".*?\"|'.*?'|[^'\"])*?>", "");
	}

	/**
	 * <p>引数の文字列から改行を除去した文字列を返却する。</p>
	 * @param target
	 * @return 改行を除去した文字列
	 */
	public static String removeLineSeparator(String target) {
	    if( target == null ) throw new IllegalArgumentException("Method argument 'target' is null.") ;
	    return target.replaceAll("\r\n|[\n\r\u2028\u2029\u0085]", "");
	}

    /**
     * 引数の文字列(UTF-8)を、Shift_JISにエンコードする。
     *
     * @param str 変換対象の文字列
     * @return エンコードされた文字列
     */
    public static String convertUtf8ToSjis(final String str) throws UnsupportedEncodingException {
    	// 変換対象をUTF-8のバイトシーケンスにエンコードしバイト配列に格納
        byte[] srcStream = str.getBytes("UTF-8");
        // エンコード
        String  ret = utf8ToSjis(new String(srcStream, "UTF-8"));
    	// 変換後文字列をSJISのバイトシーケンスにエンコードしバイト配列に格納
        byte[] destStream = ret.getBytes("SJIS");
        return new String(destStream, "SJIS");
    }

    /**
     * 引数の文字列を、エンコードする。
     *
     * @param str 変換対象の文字列
     * @return エンコードされた文字列
     */
    public static String utf8ToSjis(final String str) throws UnsupportedEncodingException {
        Map<String, String> conversion = createConversionMap();
        char oldChar;
        char newChar;
        String key;
        String ret = str;
        for (Iterator<String> itr = conversion.keySet().iterator() ; itr.hasNext() ;) {
            key = itr.next();
            oldChar = toChar(key);
            newChar = toChar(conversion.get(key));
            ret = ret.replace(oldChar, newChar);
        }
        return ret;
    }

    /**
     * エンコード情報を作成する
     *
     * @return エンコードされた文字列
     */
    public static Map<String, String> createConversionMap() throws UnsupportedEncodingException {
        Map<String, String> conversion = new HashMap<String, String>();
        // －（全角マイナス）
        conversion.put("U+FF0D", "U+2212");
        // ～（全角チルダ）
        conversion.put("U+FF5E", "U+301C");
        // ￠（セント）
        conversion.put("U+FFE0", "U+00A2");
        // ￡（ポンド）
        conversion.put("U+FFE1", "U+00A3");
        // ￢（ノット）
        conversion.put("U+FFE2", "U+00AC");
        // ―（全角マイナスより少し幅のある文字）
        conversion.put("U+2015", "U+2014");
        // ∥（半角パイプが2つ並んだような文字）
        conversion.put("U+2225", "U+2016");
        return conversion;
    }


    /**
     * 引数の文字列を、エンコードする。
     *
     * @param str 変換対象の文字列
     * @return エンコードされた文字列
     */
    public static String convertCharCodePoint(final String str) {
        Map<String, String> conversion = createCharCodePointConversionMap();
        char oldChar;
        char newChar;
        String key;
        String ret = str;
        for (Iterator<String> itr = conversion.keySet().iterator() ; itr.hasNext() ;) {
            key = itr.next();
            oldChar = toChar(key);
            newChar = toChar(conversion.get(key));
            ret = ret.replace(oldChar, newChar);
        }
        return ret;
    }

    /**
     * エンコード情報を作成する
     *
     * @return エンコードされた文字列
     */
    public static Map<String, String> createCharCodePointConversionMap() {
        Map<String, String> conversion = new HashMap<String, String>();
        // －（全角マイナス）
        conversion.put("U+2212", "U+FF0D");
        // ～（全角チルダ）
        conversion.put("U+301C", "U+FF5E");
        // ￠（セント）
        conversion.put("U+00A2", "U+FFE0");
        // ￡（ポンド）
        conversion.put("U+00A3", "U+FFE1");
        // ￢（ノット）
        conversion.put("U+00AC", "U+FFE2");
        // ―（全角マイナスより少し幅のある文字）
        conversion.put("U+2014", "U+2015");
        // ∥（半角パイプが2つ並んだような文字）
        conversion.put("U+2016", "U+2225");
        return conversion;
    }

    /**
     * 16進表記の文字を取得する。
     *
     * @param value 変換対象の文字列
     * @return 16進表記の文字
     */
    public static char toChar(final String value) {
        return (char)Integer.parseInt(value.trim().substring("U+".length()), 16);
    }

    /**
     * <p>指定した文字列を、指定したバイトまで切り出して返却します。</p>
     * @param str 変換対象文字列
     * @param cutBytes 指定したバイト数
     * @param encoding 文字エンコード
     * @return 変換後文字列
     */
	public static String cutString(final String str, final int cutBytes, final String encoding) {

		try {
			// 指定文字列が範囲を超えていない場合
			if (str == null   || str.length() == 0 || cutBytes <= 0 || str.getBytes(encoding).length <= cutBytes) {
				return str;
			}

			// 指定範囲まで切り出した文字列
			StringBuilder cutSb = new StringBuilder();
			// 指定された範囲までの一時的な文字列
			StringBuilder tmpSb = new StringBuilder();
			// 指定した文字列の数だけループ
			for (int i = 0; i < str.length(); i++) {
				// 検証文字列から1文字づつ取り出す
				String cut = str.substring(i, i + 1);
				// 指定範囲を超えたらループを終了
				if (cutBytes < tmpSb.toString().getBytes(encoding).length + cut.getBytes(encoding).length) {
					// 一時的な文字列を指定範囲の文字列として追加
					cutSb.append(tmpSb.toString());
					break;
				}
				// 一時的な文字列に追加
				tmpSb.append(cut);
			}
			// 指定された範囲の文字列を返却
			return cutSb.toString();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return str;
		}
	}

	/**
	 * <p>Getパラメータから指定されたパラメータを削除して返却します。</p>
	 * @param queryString
	 * @param removeKey
	 * @return 指定パラメータ削除後Getパラメータ
	 */
	public static String queryStringRemoveKey (final String queryString, final String removeKey) {

		// 引数チェック
		if (StringUtils.isNullOrEmpty(queryString) || StringUtils.isNullOrEmpty(removeKey)) {
			return queryString;
		}

		// Getパラメータ変換後の文字列を格納する変数
		String tmpQueryString = "";

		// Getパラメータを「&」で分割
		String[] querys = queryString.split("&");

		// Getパラメータ分ループ
		for ( String query: querys ) {

			// パラメータをキーと値に分割
			String[] params = query.split("=");

			// パラメータ != NULL && パラメータ数 = 2
			if (params != null && params.length == 2) {

				// 削除キー != パラメータのキー
				if ( !removeKey.equals(params[0]) ) {

					// Getパラメータ変換後文字列の文字数 > 0
					if ( tmpQueryString.length() > 0 ) {
						// Getパラメータ変換後文字列に「&」を追加
						tmpQueryString += "&";
					}
					// Getパラメータ変換後文字列にパラメータを追加
					tmpQueryString += query;
				}
			}
		}
		// Getパラメータ変換後文字列を返却
		return tmpQueryString;
	}

	/**
	 * テキストを1行あたりの最大文字数で分割して返却する。
	 *
	 * @param text テキスト
	 * @param maxCharLenOfRow １行あたりの最大文字数
	 * @return 分割後のテキスト
	 */
	public static List<String> getRowList(final String text, final float maxCharLenOfRow) {

		List<String> rowList = new ArrayList<String>();
		if (text == null) {
			return rowList;
		}

        if ("".equals(text)) {
        	rowList.add(text);
            return rowList;
        }

		// 全角１文字、半角２文字として一行辺りの最大文字数で分割します。
		float charNum = 0;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < text.length(); i++) {

			sb.append(text.charAt(i));

			if (isHankaku(text.charAt(i))) {
				charNum += 0.5;
			} else {
				charNum += 1;
			}

			if (charNum >= maxCharLenOfRow) {
				rowList.add(sb.toString());
				sb = new StringBuilder();
				charNum = 0;
			}

			if (maxCharLenOfRow-0.5 == charNum) {
				try {
					if (i+1 != text.length()) {
						if (!isHankaku(text.charAt(i+1))) {
							rowList.add(sb.toString());
							sb = new StringBuilder();
							charNum = 0;
						}
					}

				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

        if (charNum > 0) {
            rowList.add(sb.toString());
        }

		return rowList;
	}

	/**
	 * 指定された文字が半角か判定し結果を返却します。
	 * @param c
	 * @return
	 */
	public static boolean isHankaku(char c) {
        if( ( c<='\u007e' )|| // 英数字
            ( c=='\u00a5' )|| // \記号
            ( c=='\u203e' )|| // ~記号
            ( c>='\uff61' && c<='\uff9f' ) // 半角カナ
        ) {
        	return true;
        }
		return false;
    }

	/**
	 * 指定された文字列の文字数分ブランク文字列を返却します。
	 * @param value
	 * @param blankChar
	 * @return
	 */
	public static String changeBlankChar(final String value, final String blankChar) {

		if (StringUtils.isNullOrEmpty(value)) {
			return null;
		}

		String ret = "";
		for (int i = 0; i < value.length(); i++) {
			ret += blankChar;
		}

		return ret;
	}

	/**
     * 指定した文字列がASCII文字のみから構成されるか判断する
     *
     * @param value 対象文字列
     * @return trueならASCII文字のみ 空の場合は常にtrueとなる
     */
	public static Boolean isAscii(String value) {
	    if ( value == null || value.length() == 0 ) {
	        return true;
	    }

	    if (value.matches("\\p{ASCII}*")) {
	    	return true;
	    }
	    return false;
	}
}
