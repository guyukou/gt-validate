function user_response(a, b) {
    for (var c = b.slice(32), d = [], e = 0; e < c.length; e++) {
        var f = c.charCodeAt(e);
        d[e] = f > 57 ? f - 87 : f - 48
    }
    c = 36 * d[0] + d[1];
    var g = Math.round(a) + c;
    b = b.slice(0, 32);
    var h, i = [
            [],
            [],
            [],
            [],
            []
        ],
        j = {}, k = 0;
    e = 0;
    for (var l = b.length; e < l; e++) h = b.charAt(e), j[h] || (j[h] = 1, i[k].push(h), k++, k = 5 == k ? 0 : k);
    for (var m, n = g, o = 4, p = "", q = [1, 2, 5, 10, 50]; n > 0;) n - q[o] >= 0 ? (m = parseInt(Math.random() * i[o].length, 10), p += i[o][m], n -= q[o]) : (i.splice(o, 1), q.splice(o, 1), o -= 1);
    return p
}

function d(a) {
    var b = "()*,-./0123456789:?@ABCDEFGHIJKLMNOPQRSTUVWXYZ_abcdefghijklmnopqr",
        c = b.length,
        d = "",
        e = Math.abs(a),
        f = parseInt(e / c);
    f >= c && (f = c - 1), f && (d = b.charAt(f)), e %= c;
    var g = "";
    return a < 0 && (g += "!"), d && (g += "$"), g + d + b.charAt(e)
}
function e(a) {
    for (var b = [
        [1, 0],
        [2, 0],
        [1, -1],
        [1, 1],
        [0, 1],
        [0, -1],
        [3, 0],
        [2, -1],
        [2, 1]
    ], c = "stuvwxyz~", d = 0, e = b.length; d < e; d++) if (a[0] == b[d][0] && a[1] == b[d][1]) return c[d];
    return 0
}
function getA(arrStr) {
    eval("var a = " + arrStr);
    for (var b, f = a, g = [], h = [], i = [], j = 0, k = f.length; j < k; j++) b = e(f[j]), b ? h.push(b) : (g.push(d(f[j][0])), h.push(d(f[j][1]))), i.push(d(f[j][2]));
    return encodeURIComponent(g.join("") + "!!" + h.join("") + "!!" + i.join(""));
}
