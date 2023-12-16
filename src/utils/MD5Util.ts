// https://blog.csdn.net/github_37759996/article/details/90370807

type ResultType = string[] | Array<string | number>[] | string

export default class MD5Util {
    private static _inst: MD5Util;

    public static get Instance(): MD5Util {
        return this._inst || (this._inst = new MD5Util());
    }

    public get_md5(str: string): string {
        return this.md5(str);
    }

    split(target: string | Array<string | number>, step: number, markString: boolean = typeof target === "string") {
        if (typeof target === "string") target = target.split("");
        let result: ResultType = target.map(
            (_, index: number) =>
                index % step === 0
                    ? Array.from(Array(step).keys()).map((x: number) => target[index + x])
                    : []
        )
            .filter(x => x.length > 0);
        if (markString) result = result.map(x => x.join(""))
        return result;
    }

    padding(str: string | string[], length: number, char: string, tail = true, isArray: boolean = Array.isArray(str)) {
        let arr;
        if (Array.isArray(str)) {
            arr = str;
        } else {
            arr = str.split("");
        }
        const paddingStr: string[] = this.range(length - str.length).map(() => char);
        const result = tail ? arr.concat(paddingStr) : paddingStr.concat(arr);
        return isArray ? result : result.join("");
    }

    little_endian(charCode: number) {
        return this.split(this.padding(charCode.toString(16), 8, "0", false), 2).reverse().join("");
    }

    range(...args: number[]) {
        const start: number = args.length === 1 ? 0 : args[0];
        const end: number = args.length === 2 ? args[1] : args[0] - 1;
        return Array.from(Array(end - start + 1).keys()).map((x: number) => x + start);
    }

    to_binary(code: number, bit = 8, max: number = Math.pow(2, bit) - 1) {
        if (code < 0) throw new Error("code should be greater than: 0");
        if (code > max) throw new Error("code should be less than: " + max);
        return this.padding(code.toString(2), bit, "0", false);
    }

    utf16_to_utf8(str: string) {
        return str.split("").map((char: string) => this.utf8_encode(char)).join("");
    }

    utf8_encode(char: string) {
        let utf_text = "";
        const c = char.charCodeAt(0);
        if (c < 128) {
            utf_text += String.fromCharCode(c);
        } else if ((c > 127) && (c < 2048)) {
            utf_text += String.fromCharCode((c >> 6) | 0b11000000);
            utf_text += String.fromCharCode((c & 0b00111111) | 0b10000000);
        } else {
            utf_text += String.fromCharCode((c >> 12) | 0b11100000);
            utf_text += String.fromCharCode(((c >> 6) & 0b00111111) | 0b10000000);
            utf_text += String.fromCharCode((c & 0b00111111) | 0b10000000);
        }
        return utf_text;
    }

    uint_add(...args: number[]) {
        const t = Uint32Array.from([0]);
        const x = Uint32Array.from(args);
        x.forEach(n => t[0] = t[0] + n);
        return t[0];
    }

    loop_shift_left(n: number, bits: number) {
        return (n << bits) | (n >>> (32 - bits));
    }

    A = 0x67452301;
    B = 0xefcdab89;
    C = 0x98badcfe;
    D = 0x10325476;

    F(b: number, c: number, d: number) {
        return (b & c) | (~b & d);
    }

    G(b: number, c: number, d: number) {
        return (b & d) | (c & ~d);
    }

    H(b: number, c: number, d: number) {
        return b ^ c ^ d;
    }

    I(b: number, c: number, d: number) {
        return c ^ (b | ~d)
    }

    S = [
        7, 12, 17, 22, 7, 12, 17, 22, 7, 12, 17, 22, 7, 12, 17, 22,
        5, 9, 14, 20, 5, 9, 14, 20, 5, 9, 14, 20, 5, 9, 14, 20,
        4, 11, 16, 23, 4, 11, 16, 23, 4, 11, 16, 23, 4, 11, 16, 23,
        6, 10, 15, 21, 6, 10, 15, 21, 6, 10, 15, 21, 6, 10, 15, 21
    ]

    T(i: number) {
        return Math.floor(Math.pow(2, 32) * Math.abs(Math.sin(i + 1)));
    }

    x_index(i: number) {
        if (i >= 0 && i <= 15) return i;
        if (i >= 16 && i <= 31) return (5 * i + 1) % 16;
        if (i >= 32 && i <= 47) return (3 * i + 5) % 16;
        if (i >= 48 && i <= 63) return (7 * i) % 16;
        return 0;
    }

    wrap(m?: (b: number, c: number, d: number) => number) {
        if (!m) return undefined
        return (a: number, b: number, c: number, d: number, x: number, s: number, t: number) => {
            // 循环左移
            return this.uint_add(this.loop_shift_left(this.uint_add(a, m(b, c, d), x, t), s), b);
        };
    }

    process_message(str: string) {
        const length = str.length;
        const length_of_zero = Math.ceil(length / 64) * 64 - length - 8 - 1;
        str += String.fromCharCode(0b10000000);
        const strArray = this.padding(str.split(""), length + 1 + length_of_zero, String.fromCharCode(0));
        const tail = this.split(this.padding(this.to_binary(length * 8 % Math.pow(2, 64)), 64, "0"), 8).map(x => parseInt(x.toString(), 2));
        const head = (strArray as string[]).map(x => x.charCodeAt(0));
        return Uint32Array.from(
            this.split(head.concat(tail), 4)
                .map(x =>
                    (x as number[]).map((t: number) => this.padding(t.toString(16), 2, "0", false)).join("")
                )
                .map(x => parseInt(x, 16))
                .map(x => parseInt(this.little_endian(x), 16))
        )
    }

    fghi(i: number) {
        if (i >= 0 && i <= 15) return this.F;
        if (i >= 16 && i <= 31) return this.G;
        if (i >= 32 && i <= 47) return this.H;
        if (i >= 48 && i <= 63) return this.I;
    }

    fghi_wrapped(i: number) {
        return this.wrap(this.fghi(i));
    }

    //------------------------------------------------
    md5(str: string) {
        str = this.utf16_to_utf8(str);
        const uint32_array = this.process_message(str);
        const result = Uint32Array.from([this.A, this.B, this.C, this.D]);
        const chunks = this.split(Array.from(uint32_array), 16);
        for (const chunk of chunks) {
            const a = result[0];
            const b = result[1];
            const c = result[2];
            const d = result[3];
            for (let i = 0; i < 64; i++) {
                const temp = this.fghi_wrapped(i)
                if (temp === undefined) continue
                result[(4 - i % 4) % 4] = temp(result[(4 - i % 4) % 4], result[((4 - i % 4) + 1) % 4], result[((4 - i % 4) + 2) % 4], result[((4 - i % 4) + 3) % 4], chunk[this.x_index(i)] as number, this.S[i], this.T(i))
            }
            result[0] = a + result[0];
            result[1] = b + result[1];
            result[2] = c + result[2];
            result[3] = d + result[3];
        }
        return Array.from(result).map(x => this.little_endian(x)).join("").toLowerCase();
    }
}
