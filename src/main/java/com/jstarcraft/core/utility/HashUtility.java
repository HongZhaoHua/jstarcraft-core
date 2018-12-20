package com.jstarcraft.core.utility;

/**
 * 哈希工具
 * 
 * <pre>
 * 整数哈希的目标
 * 1.函数要是可逆的(1对1的映射)
 * 2.雪崩效应(输入中1bit的变化 影响 输出中1/4 到 1/2的bits变化)
 * </pre>
 * 
 * @author Birdy
 *
 */
public class HashUtility {

	// 数值哈希部分

	/**
	 * MurmurHash2, by Austin Appleby
	 * 
	 * <pre>
	 * https://github.com/sangupta/murmur/tree/master/src/main/java/com/sangupta/murmur
	 * https://github.com/addthis/stream-lib/blob/master/src/main/java/com/clearspring/analytics/hash/MurmurHash.java
	 * </pre>
	 * 
	 * @param data
	 * @return
	 */
	public static int murmur2NumberHash32(int data) {
		int m = 0x5bd1e995;
		int r = 24;

		int hash = 0;

		int k = data * m;
		k ^= k >>> r;
		hash ^= k * m;

		k = (data >> 32) * m;
		k ^= k >>> r;
		hash *= m;
		hash ^= k * m;

		hash ^= hash >>> 13;
		hash *= m;
		hash ^= hash >>> 15;

		return hash;
	}

	/**
	 * MurmurHash3, by Austin Appleby
	 * 
	 * <pre>
	 * https://github.com/sangupta/murmur/tree/master/src/main/java/com/sangupta/murmur
	 * https://github.com/yonik/java_util/blob/master/src/util/hash/MurmurHash3.java
	 * </pre>
	 * 
	 * @param data
	 * @return
	 */
	public static int murmur3NumberHash32(int data) {
		data ^= data >>> 16;
		data *= 0x85ebca6b;
		data ^= data >>> 13;
		data *= 0xc2b2ae35;
		data ^= data >>> 16;
		return data;
	}

	/**
	 * Robert Jenkins' 32 bit integer hash function
	 * 
	 * @param data
	 * @return
	 */
	public static int rjNumberHash32(int data) {
		data = (data + 0x7ed55d16) + (data << 12);
		data = (data ^ 0xc761c23c) ^ (data >> 19);
		data = (data + 0x165667b1) + (data << 5);
		data = (data + 0xd3a2646c) ^ (data << 9);
		data = (data + 0xfd7046c5) + (data << 3);
		data = (data ^ 0xb55a4f09) ^ (data >> 16);
		return data;
	}

	/**
	 * Thomas Wang' 32 bit integer hash function
	 */
	public static int twNumberHash32(int data) {
		data += ~(data << 15);
		data ^= (data >>> 10);
		data += (data << 3);
		data ^= (data >>> 6);
		data += ~(data << 11);
		data ^= (data >>> 16);
		return data;
	}

	// 字符串哈希部分

	/**
	 * 加法哈希
	 * 
	 * @param data
	 * @return
	 */
	public static int additiveStringHash32(String data) {
		int size = data.length();
		int hash = data.length();
		for (int index = 0; index < size; index++) {
			hash += data.charAt(index);
		}
		return hash;
	}

	/**
	 * <pre>
	 * An algorithm produced by Arash Partow. 
	 * I took ideas from all of the above hash functions making a hybrid rotative and additive hash function algorithm. There isn't any real mathematical analysis explaining why one should use this hash function instead of the others described above other than the fact that I tired to resemble the design as close as possible to a simple LFSR. An empirical result which demonstrated the distributive abilities of the hash algorithm was obtained using a hash-table with 100003 buckets, hashing The Project Gutenberg Etext of Webster's Unabridged Dictionary, the longest encountered chain length was 7, the average chain length was 2, the number of empty buckets was 4579.
	 * </pre>
	 * 
	 * @param data
	 * @return
	 */
	public static int apStringHash32(String data) {
		int size = data.length();
		int hash = 0xaaaaaaaa;
		for (int index = 0; index < size; index++) {
			if ((index & 1) == 0) {
				hash ^= ((hash << 7) ^ data.charAt(index) * (hash >> 3));
			} else {
				hash ^= (~((hash << 11) + data.charAt(index) ^ (hash >> 5)));
			}
		}
		return hash;
	}

	/**
	 * 乘法哈希
	 * 
	 * @param value
	 * @return
	 */
	public static int bernsteinStringHash32(String data) {
		int size = data.length();
		int hash = 0;
		for (int index = 0; index < size; index++) {
			hash = 33 * hash + data.charAt(index);
		}
		return hash;
	}

	/**
	 * <pre>
	 * This hash function comes from Brian Kernighan and Dennis Ritchie's book "The C Programming Language". 
	 * It is a simple hash function using a strange set of possible seeds which all constitute a pattern of 31....31...31 etc, it seems to be very similar to the DJB hash function.
	 * </pre>
	 * 
	 * @param data
	 * @return
	 */
	public static int bkdrStringHash32(String data) {
		int size = data.length();
		int seed = 131; // 31 131 1313 13131 131313 etc..
		int hash = 0;
		for (int index = 0; index < size; index++) {
			hash = (hash * seed) + data.charAt(index);
		}
		return hash;
	}

	/**
	 * <pre>
	 * https://github.com/bennybp
	 * </pre>
	 * 
	 * @param data
	 * @return
	 */
	// TODO 注意bpStringHash32从long改到int的时候,单元测试的冲突变化非常大.
	// public static long bpStringHash32(String data) {
	// int size = data.length();
	// long hash = 0;
	// for (int index = 0; index < size; index++) {
	// hash = hash << 7 ^ data.charAt(index);
	// }
	// return hash;
	// }
	public static int bpStringHash32(String data) {
		int size = data.length();
		int hash = 0;
		for (int index = 0; index < size; index++) {
			hash = hash << 7 ^ data.charAt(index);
		}
		return hash;
	}

	private static int[] crcTable = {

			0x00000000, 0x77073096, 0xee0e612c, 0x990951ba, 0x076dc419, 0x706af48f, 0xe963a535, 0x9e6495a3,

			0x0edb8832, 0x79dcb8a4, 0xe0d5e91e, 0x97d2d988, 0x09b64c2b, 0x7eb17cbd, 0xe7b82d07, 0x90bf1d91,

			0x1db71064, 0x6ab020f2, 0xf3b97148, 0x84be41de, 0x1adad47d, 0x6ddde4eb, 0xf4d4b551, 0x83d385c7,

			0x136c9856, 0x646ba8c0, 0xfd62f97a, 0x8a65c9ec, 0x14015c4f, 0x63066cd9, 0xfa0f3d63, 0x8d080df5,

			0x3b6e20c8, 0x4c69105e, 0xd56041e4, 0xa2677172, 0x3c03e4d1, 0x4b04d447, 0xd20d85fd, 0xa50ab56b,

			0x35b5a8fa, 0x42b2986c, 0xdbbbc9d6, 0xacbcf940, 0x32d86ce3, 0x45df5c75, 0xdcd60dcf, 0xabd13d59,

			0x26d930ac, 0x51de003a, 0xc8d75180, 0xbfd06116, 0x21b4f4b5, 0x56b3c423, 0xcfba9599, 0xb8bda50f,

			0x2802b89e, 0x5f058808, 0xc60cd9b2, 0xb10be924, 0x2f6f7c87, 0x58684c11, 0xc1611dab, 0xb6662d3d,

			0x76dc4190, 0x01db7106, 0x98d220bc, 0xefd5102a, 0x71b18589, 0x06b6b51f, 0x9fbfe4a5, 0xe8b8d433,

			0x7807c9a2, 0x0f00f934, 0x9609a88e, 0xe10e9818, 0x7f6a0dbb, 0x086d3d2d, 0x91646c97, 0xe6635c01,

			0x6b6b51f4, 0x1c6c6162, 0x856530d8, 0xf262004e, 0x6c0695ed, 0x1b01a57b, 0x8208f4c1, 0xf50fc457,

			0x65b0d9c6, 0x12b7e950, 0x8bbeb8ea, 0xfcb9887c, 0x62dd1ddf, 0x15da2d49, 0x8cd37cf3, 0xfbd44c65,

			0x4db26158, 0x3ab551ce, 0xa3bc0074, 0xd4bb30e2, 0x4adfa541, 0x3dd895d7, 0xa4d1c46d, 0xd3d6f4fb,

			0x4369e96a, 0x346ed9fc, 0xad678846, 0xda60b8d0, 0x44042d73, 0x33031de5, 0xaa0a4c5f, 0xdd0d7cc9,

			0x5005713c, 0x270241aa, 0xbe0b1010, 0xc90c2086, 0x5768b525, 0x206f85b3, 0xb966d409, 0xce61e49f,

			0x5edef90e, 0x29d9c998, 0xb0d09822, 0xc7d7a8b4, 0x59b33d17, 0x2eb40d81, 0xb7bd5c3b, 0xc0ba6cad,

			0xedb88320, 0x9abfb3b6, 0x03b6e20c, 0x74b1d29a, 0xead54739, 0x9dd277af, 0x04db2615, 0x73dc1683,

			0xe3630b12, 0x94643b84, 0x0d6d6a3e, 0x7a6a5aa8, 0xe40ecf0b, 0x9309ff9d, 0x0a00ae27, 0x7d079eb1,

			0xf00f9344, 0x8708a3d2, 0x1e01f268, 0x6906c2fe, 0xf762575d, 0x806567cb, 0x196c3671, 0x6e6b06e7,

			0xfed41b76, 0x89d32be0, 0x10da7a5a, 0x67dd4acc, 0xf9b9df6f, 0x8ebeeff9, 0x17b7be43, 0x60b08ed5,

			0xd6d6a3e8, 0xa1d1937e, 0x38d8c2c4, 0x4fdff252, 0xd1bb67f1, 0xa6bc5767, 0x3fb506dd, 0x48b2364b,

			0xd80d2bda, 0xaf0a1b4c, 0x36034af6, 0x41047a60, 0xdf60efc3, 0xa867df55, 0x316e8eef, 0x4669be79,

			0xcb61b38c, 0xbc66831a, 0x256fd2a0, 0x5268e236, 0xcc0c7795, 0xbb0b4703, 0x220216b9, 0x5505262f,

			0xc5ba3bbe, 0xb2bd0b28, 0x2bb45a92, 0x5cb36a04, 0xc2d7ffa7, 0xb5d0cf31, 0x2cd99e8b, 0x5bdeae1d,

			0x9b64c2b0, 0xec63f226, 0x756aa39c, 0x026d930a, 0x9c0906a9, 0xeb0e363f, 0x72076785, 0x05005713,

			0x95bf4a82, 0xe2b87a14, 0x7bb12bae, 0x0cb61b38, 0x92d28e9b, 0xe5d5be0d, 0x7cdcefb7, 0x0bdbdf21,

			0x86d3d2d4, 0xf1d4e242, 0x68ddb3f8, 0x1fda836e, 0x81be16cd, 0xf6b9265b, 0x6fb077e1, 0x18b74777,

			0x88085ae6, 0xff0f6a70, 0x66063bca, 0x11010b5c, 0x8f659eff, 0xf862ae69, 0x616bffd3, 0x166ccf45,

			0xa00ae278, 0xd70dd2ee, 0x4e048354, 0x3903b3c2, 0xa7672661, 0xd06016f7, 0x4969474d, 0x3e6e77db,

			0xaed16a4a, 0xd9d65adc, 0x40df0b66, 0x37d83bf0, 0xa9bcae53, 0xdebb9ec5, 0x47b2cf7f, 0x30b5ffe9,

			0xbdbdf21c, 0xcabac28a, 0x53b39330, 0x24b4a3a6, 0xbad03605, 0xcdd70693, 0x54de5729, 0x23d967bf,

			0xb3667a2e, 0xc4614ab8, 0x5d681b02, 0x2a6f2b94, 0xb40bbe37, 0xc30c8ea1, 0x5a05df1b, 0x2d02ef8d

	};

	/**
	 * CRC哈希
	 * 
	 * @param data
	 * @return
	 */
	public static int crcStringHash32(String data) {
		int size = data.length();
		int hash = data.length();
		for (int index = 0; index < size; index++) {
			hash = (hash >> 8) ^ crcTable[(hash & 0xff) ^ data.charAt(index)];
		}
		return hash;
	}

	/**
	 * <pre>
	 * An algorithm proposed by Donald E. Knuth in The Art Of Computer Programming Volume 3, under the topic of sorting and search chapter 6.4.
	 * </pre>
	 * 
	 * @param data
	 * @return
	 */
	public static int dekStringHash32(String data) {
		int size = data.length();
		int hash = data.length();
		for (int index = 0; index < size; index++) {
			hash = ((hash << 5) ^ (hash >> 27)) ^ data.charAt(index);
		}
		return hash;
	}

	/**
	 * <pre>
	 * An algorithm produced by Professor Daniel J. Bernstein and shown first to the world on the usenet newsgroup comp.lang.c. 
	 * It is one of the most efficient hash functions ever published.
	 * </pre>
	 * 
	 * @param data
	 * @return
	 */
	public static int djbStringHash32(String data) {
		int size = data.length();
		int hash = 5381;
		for (int index = 0; index < size; index++) {
			hash = ((hash << 5) + hash) + data.charAt(index);
		}
		return hash;
	}

	/**
	 * <pre>
	 * Similar to the PJW Hash function, but tweaked for 32-bit processors. 
	 * It is a widely used hash function on UNIX based systems.
	 * </pre>
	 * 
	 * @param data
	 * @return
	 */
	public static int elfStringHash32(String data) {
		int size = data.length();
		int hash = 0;
		int mask = 0;
		for (int index = 0; index < size; index++) {
			hash = (hash << 4) + data.charAt(index);
			if ((mask = hash & 0xF0000000) != 0) {
				hash ^= (mask >> 24);
			}
			hash &= ~mask;
		}
		return hash;
	}

	/**
	 * <pre>
	 * http://www.isthe.com/chongo/tech/comp/fnv/
	 * </pre>
	 * 
	 * @param data
	 * @return
	 */
	public static int fnv0StringHash32(String data) {
		int size = data.length();
		int prime = 0x811c9dc5;
		int hash = 0;
		for (int index = 0; index < size; index++) {
			hash *= prime;
			hash ^= data.charAt(index);
		}
		return hash;
	}

	/**
	 * FNV1哈希
	 * 
	 * @param data
	 * @return
	 */
	public static int fnv1StringHash32(String data) {
		int prime = 16777619;
		int hash = 0x811c9dc5;
		for (int index = 0; index < data.length(); index++) {
			hash = (hash ^ data.charAt(index)) * prime;
		}
		hash += hash << 13;
		hash ^= hash >> 7;
		hash += hash << 3;
		hash ^= hash >> 17;
		hash += hash << 5;
		return hash;
	}

	/**
	 * <pre>
	 * A bitwise hash function written by Justin Sobel.
	 * </pre>
	 * 
	 * @param data
	 * @return
	 */
	public static int jsStringHash32(String data) {
		int size = data.length();
		int hash = 1315423911;
		for (int index = 0; index < size; index++) {
			hash ^= ((hash << 5) + data.charAt(index) + (hash >> 2));
		}
		return hash;
	}

	/**
	 * MurmurHash1, by Austin Appleby
	 * 
	 * <pre>
	 * https://github.com/sangupta/murmur/tree/master/src/main/java/com/sangupta/murmur
	 * </pre>
	 * 
	 * @param data
	 * @return
	 */
	public static int murmur1StringHash32(String data) {
		byte[] bytes = data.getBytes(StringUtility.CHARSET);
		int m = 0xc6a4a793;
		int r = 16;

		int length = bytes.length;
		int seed = 0;
		int hash = seed ^ (length * m);

		// Mix 4 bytes at a time into the hash
		int length4 = length >> 2;

		for (int i = 0; i < length4; i++) {
			int i4 = i << 2;

			int k = (bytes[i4] & 0xff);
			k |= (bytes[i4 + 1] & 0xff) << 8;
			k |= (bytes[i4 + 2] & 0xff) << 16;
			k |= (bytes[i4 + 3] & 0xff) << 24;

			hash = ((hash + k) & 0xffffffff);
			hash = ((hash * m) & 0xffffffff);
			hash ^= ((hash >> 16) & 0xffffffff);
		}

		// remaining bytes
		int offset = length4 << 2;
		switch (length & 3) {
		case 3:
			hash += ((bytes[offset + 2] << 16) & 0xffffffff);

		case 2:
			hash += ((bytes[offset + 1] << 8) & 0xffffffff);

		case 1:
			hash += ((bytes[offset]) & 0xffffffff);
			hash = ((hash * m) & 0xffffffff);
			hash ^= ((hash >> r) & 0xffffffff);
		}

		// operations
		hash = ((hash * m) & 0xffffffff);
		hash ^= ((hash >> 10) & 0xffffffff);
		hash = ((hash * m) & 0xffffffff);
		hash ^= ((hash >> 17) & 0xffffffff);

		// return the hash
		return hash;
	}

	/**
	 * MurmurHash2, by Austin Appleby
	 * 
	 * <pre>
	 * https://github.com/sangupta/murmur/tree/master/src/main/java/com/sangupta/murmur
	 * https://github.com/addthis/stream-lib/blob/master/src/main/java/com/clearspring/analytics/hash/MurmurHash.java
	 * </pre>
	 * 
	 * @param data
	 * @return
	 */
	public static int murmur2StringHash32(String data) {
		byte[] bytes = data.getBytes(StringUtility.CHARSET);
		int length = bytes.length;
		int seed = 0x9747b28c;
		// 'm' and 'r' are mixing constants generated offline.
		// They're not really 'magic', they just happen to work well.
		int m = 0x5bd1e995;
		int r = 24;
		// Initialize the hash to a random value
		int hash = seed ^ length;
		int length4 = length / 4;
		for (int i = 0; i < length4; i++) {
			int i4 = i * 4;
			int k = (bytes[i4 + 0] & 0xff) + ((bytes[i4 + 1] & 0xff) << 8) + ((bytes[i4 + 2] & 0xff) << 16) + ((bytes[i4 + 3] & 0xff) << 24);
			k *= m;
			k ^= k >>> r;
			k *= m;
			hash *= m;
			hash ^= k;
		}
		// Handle the last few bytes of the input array
		switch (length % 4) {
		case 3:
			hash ^= (bytes[(length & ~3) + 2] & 0xff) << 16;
		case 2:
			hash ^= (bytes[(length & ~3) + 1] & 0xff) << 8;
		case 1:
			hash ^= (bytes[length & ~3] & 0xff);
			hash *= m;
		}
		hash ^= hash >>> 13;
		hash *= m;
		hash ^= hash >>> 15;
		return hash;
	}

	/**
	 * MurmurHash3, by Austin Appleby
	 * 
	 * <pre>
	 * https://github.com/sangupta/murmur/tree/master/src/main/java/com/sangupta/murmur
	 * https://github.com/yonik/java_util/blob/master/src/util/hash/MurmurHash3.java
	 * </pre>
	 * 
	 * @param data
	 * @return
	 */
	public static int murmur3StringHash32(String data) {
		int c1 = 0xcc9e2d51;
		int c2 = 0x1b873593;

		int offset = 0;
		int len = data.length();
		int seed = 0;
		int hash = seed;

		int pos = offset;
		int end = offset + len;
		int k1 = 0;
		int k2 = 0;
		int shift = 0;
		int bits = 0;
		int nBytes = 0; // length in UTF8 bytes

		while (pos < end) {
			int code = data.charAt(pos++);
			if (code < 0x80) {
				k2 = code;
				bits = 8;

				/***
				 * // optimized ascii implementation (currently slower!!! code size?) if (shift
				 * == 24) { k1 = k1 | (code << 24); k1 *= c1; k1 = (k1 << 15) | (k1 >>> 17); //
				 * ROTL32(k1,15); k1 *= c2; h1 ^= k1; h1 = (h1 << 13) | (h1 >>> 19); //
				 * ROTL32(h1,13); h1 = h1*5+0xe6546b64; shift = 0; nBytes += 4; k1 = 0; } else {
				 * k1 |= code << shift; shift += 8; } continue;
				 ***/

			} else if (code < 0x800) {
				k2 = (0xC0 | (code >> 6)) | ((0x80 | (code & 0x3F)) << 8);
				bits = 16;
			} else if (code < 0xD800 || code > 0xDFFF || pos >= end) {
				// we check for pos>=end to encode an unpaired surrogate as 3 bytes.
				k2 = (0xE0 | (code >> 12)) | ((0x80 | ((code >> 6) & 0x3F)) << 8) | ((0x80 | (code & 0x3F)) << 16);
				bits = 24;
			} else {
				// surrogate pair
				// int utf32 = pos < end ? (int) data.charAt(pos++) : 0;
				int utf32 = (int) data.charAt(pos++);
				utf32 = ((code - 0xD7C0) << 10) + (utf32 & 0x3FF);
				k2 = (0xff & (0xF0 | (utf32 >> 18))) | ((0x80 | ((utf32 >> 12) & 0x3F))) << 8 | ((0x80 | ((utf32 >> 6) & 0x3F))) << 16 | (0x80 | (utf32 & 0x3F)) << 24;
				bits = 32;
			}

			k1 |= k2 << shift;

			// int used_bits = 32 - shift; // how many bits of k2 were used in k1.
			// int unused_bits = bits - used_bits; // (bits-(32-shift)) == bits+shift-32 ==
			// bits-newshift

			shift += bits;
			if (shift >= 32) {
				// mix after we have a complete word

				k1 *= c1;
				k1 = (k1 << 15) | (k1 >>> 17); // ROTL32(k1,15);
				k1 *= c2;

				hash ^= k1;
				hash = (hash << 13) | (hash >>> 19); // ROTL32(h1,13);
				hash = hash * 5 + 0xe6546b64;

				shift -= 32;
				// unfortunately, java won't let you shift 32 bits off, so we need to check for
				// 0
				if (shift != 0) {
					k1 = k2 >>> (bits - shift); // bits used == bits - newshift
				} else {
					k1 = 0;
				}
				nBytes += 4;
			}

		} // inner

		// handle tail
		if (shift > 0) {
			nBytes += shift >> 3;
			k1 *= c1;
			k1 = (k1 << 15) | (k1 >>> 17); // ROTL32(k1,15);
			k1 *= c2;
			hash ^= k1;
		}

		// ization
		hash ^= nBytes;

		// fmix(h1);
		hash ^= hash >>> 16;
		hash *= 0x85ebca6b;
		hash ^= hash >>> 13;
		hash *= 0xc2b2ae35;
		hash ^= hash >>> 16;

		return hash;
	}

	/**
	 * <pre>
	 * This hash algorithm is based on work by Peter J. Weinberger of AT&T Bell Labs. 
	 * The book Compilers (Principles, Techniques and Tools) by Aho, Sethi and Ulman, recommends the use of hash functions that employ the hashing methodology found in this particular algorithm.
	 * </pre>
	 * 
	 * @param data
	 * @return
	 */
	public static int pjwStringHash32(String data) {
		int size = data.length();
		int bits = (4 * 8);
		int threeFourth = ((bits * 3) / 4);
		int oneEighth = (bits / 8);
		int mask = (0xffffffff) << (bits - oneEighth);
		int hash = 0;
		int test = 0;
		for (int index = 0; index < size; index++) {
			hash = (hash << oneEighth) + data.charAt(index);
			if ((test = hash & mask) != 0L) {
				hash = ((hash ^ (test >> threeFourth)) & (~mask));
			}
		}
		return hash;
	}

	/**
	 * 旋转哈希
	 * 
	 * @param data
	 * @return
	 */
	public static int rotatingStringHash32(String data) {
		int size = data.length();
		int hash = data.length();
		for (int index = 0; index < size; index++) {
			hash = (hash << 4) ^ (hash >> 28) ^ data.charAt(index);
		}
		return hash;
	}

	/**
	 * <pre>
	 * A simple hash function from Robert Sedgwicks Algorithms in C book. 
	 * I've added some simple optimizations to the algorithm in order to speed up its hashing process.
	 * </pre>
	 * 
	 * @param data
	 * @return
	 */
	public static int rsStringHash32(String data) {
		int size = data.length();
		int alpha = 63689;
		int beta = 378551;
		int hash = 0;
		for (int index = 0; index < size; index++) {
			hash = hash * alpha + data.charAt(index);
			alpha = alpha * beta;
		}
		return hash;
	}

	/**
	 * <pre>
	 * This is the algorithm of choice which is used in the open source SDBM project. The hash function seems to have a good over-all distribution for many different data sets. 
	 * It seems to work well in situations where there is a high variance in the MSBs of the elements in a data set.
	 * </pre>
	 * 
	 * @param data
	 * @return
	 */
	public static int sdbmStringHash32(String data) {
		int size = data.length();
		int hash = 0;
		for (int index = 0; index < size; index++) {
			hash = data.charAt(index) + (hash << 6) + (hash << 16) - hash;
		}
		return hash;
	}

	/**
	 * Universal哈希
	 * 
	 * @param data
	 * @param table
	 * @return
	 */
	public static int universalStringHash32(String data, int[] table) {
		int size = data.length();
		int hash = data.length();
		for (int index = 0; index < (size << 3); index += 8) {
			char element = data.charAt(index >> 3);
			if ((element & 0x01) == 0)
				hash ^= table[index + 0];
			if ((element & 0x02) == 0)
				hash ^= table[index + 1];
			if ((element & 0x04) == 0)
				hash ^= table[index + 2];
			if ((element & 0x08) == 0)
				hash ^= table[index + 3];
			if ((element & 0x10) == 0)
				hash ^= table[index + 4];
			if ((element & 0x20) == 0)
				hash ^= table[index + 5];
			if ((element & 0x40) == 0)
				hash ^= table[index + 6];
			if ((element & 0x80) == 0)
				hash ^= table[index + 7];
		}
		return hash;
	}

	/**
	 * Zobrist哈希
	 * 
	 * @param data
	 * @param table
	 * @return
	 */
	public static int zobristStringHash32(String data, int[][] table) {
		int size = data.length();
		int hash = data.length();
		for (int index = 0; index < size; index++) {
			hash ^= table[index][data.charAt(index)];
		}
		return hash;
	}

}
