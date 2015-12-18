package com.wmz7year.synyed.util;

/**
 * lzf数据解压缩工具类
 * 
 * @Title: LZFDecoder.java
 * @Package org.Redis.Synyed.util
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月17日 上午9:46:44
 * @version V1.0
 */
public class LZFDecoder {
	/**
	 * The maximum number of literals in a chunk (32).
	 */
	private static final int MAX_LITERAL = 1 << 5;

	/**
	 * lzf解压缩的方法<br>
	 * 
	 * @param in
	 *            带解压的数据缓冲区
	 * @param inPos
	 *            解压数据起始位
	 * @param inLen
	 *            解压数据长度
	 * @param out
	 *            解压后数据的缓冲器
	 * @param outPos
	 *            解压后数据起始位
	 * @param outLen
	 *            解压后的长度
	 * @throws Exception
	 *             当解压发生错误时抛出该异常
	 */
	public static void decode(byte[] in, int inPos, int inLen, byte[] out, int outPos, int outLen) throws Exception {
		if (inPos < 0 || inLen < 0 || outPos < 0 || outLen < 0) {
			throw new IllegalArgumentException();
		}
		do {
			int ctrl = in[inPos++] & 255;
			if (ctrl < MAX_LITERAL) {
				// literal run of length = ctrl + 1,
				ctrl++;
				// copy to output and move forward this many bytes
				System.arraycopy(in, inPos, out, outPos, ctrl);
				outPos += ctrl;
				inPos += ctrl;
			} else {
				// back reference
				// the highest 3 bits are the match length
				int len = ctrl >> 5;
				// if the length is maxed, add the next byte to the length
				if (len == 7) {
					len += in[inPos++] & 255;
				}
				// minimum back-reference is 3 bytes,
				// so 2 was subtracted before storing size
				len += 2;

				// ctrl is now the offset for a back-reference...
				// the logical AND operation removes the length bits
				ctrl = -((ctrl & 0x1f) << 8) - 1;

				// the next byte augments/increases the offset
				ctrl -= in[inPos++] & 255;

				// copy the back-reference bytes from the given
				// location in output to current position
				ctrl += outPos;
				if (outPos + len > outLen) {
					// reduce array bounds checking
					throw new ArrayIndexOutOfBoundsException();
				}
				for (int i = 0; i < len; i++) {
					out[outPos++] = out[ctrl++];
				}
			}
		} while (outPos < outLen);
	}

}
