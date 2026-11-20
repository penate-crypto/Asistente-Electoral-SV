"""Lector mínimo de datos Kotlin: llamadas tipo Nombre(arg = valor, ...), listOf(...), textos, números y constantes.
Sirve para pasar a JSON los datos que la app Android tiene escritos en código (preguntas, casos, etc.)."""
import re

class P:
    def __init__(self, s): self.s = s; self.i = 0
    def ws(self):
        s = self.s
        while self.i < len(s):
            if s[self.i].isspace(): self.i += 1
            elif s.startswith("//", self.i): self.i = s.index("\n", self.i) if "\n" in s[self.i:] else len(s)
            elif s.startswith("/*", self.i): self.i = s.index("*/", self.i) + 2
            else: break
    def string(self):
        s = self.s
        if s.startswith('"""', self.i):
            j = s.index('"""', self.i + 3); v = s[self.i + 3:j]; self.i = j + 3
            return v
        assert s[self.i] == '"'
        j = self.i + 1; out = []
        while s[j] != '"':
            if s[j] == "\\":
                c = s[j + 1]
                if c == "u": out.append(chr(int(s[j + 2:j + 6], 16))); j += 6; continue
                out.append({"n": "\n", "t": "\t", '"': '"', "\\": "\\", "$": "$", "'": "'"}.get(c, c)); j += 2; continue
            out.append(s[j]); j += 1
        self.i = j + 1
        return "".join(out)
    def value(self):
        self.ws(); s = self.s
        if s[self.i] == '"':
            v = self.string(); self.ws()
            while s.startswith("+", self.i):          # "a" + "b"
                self.i += 1; self.ws(); v += self.string(); self.ws()
            if s.startswith(".trimIndent()", self.i): self.i += len(".trimIndent()")
            return v
        m = re.compile(r"-?\d+(?:\.\d+)?f?(?![\w.])").match(s, self.i)
        if m:
            self.i = m.end(); t = m.group(0).rstrip("f"); return float(t) if "." in t else int(t)
        m = re.compile(r"[A-Za-z_][\w.]*").match(s, self.i)
        assert m, s[self.i:self.i + 60]
        name = m.group(0); self.i = m.end()
        if name in ("true", "false"): return name == "true"
        if name == "null": return None
        self.ws()
        if s.startswith("(", self.i):
            self.i += 1
            if name in ("listOf", "mutableListOf", "arrayListOf", "setOf"):
                items = []
                while True:
                    self.ws()
                    if s[self.i] == ")": self.i += 1; break
                    items.append(self.value()); self.ws()
                    if s[self.i] == ",": self.i += 1
                return items
            args = {}; pos = []
            while True:
                self.ws()
                if s[self.i] == ")": self.i += 1; break
                m2 = re.compile(r"([A-Za-z_]\w*)\s*=(?!=)").match(s, self.i)
                if m2: self.i = m2.end(); args[m2.group(1)] = self.value()
                else: pos.append(self.value())
                self.ws()
                if s[self.i] == ",": self.i += 1
            if pos: args["_pos"] = pos
            args["_tipo"] = name
            return args
        return {"_const": name}

def llamadas(texto, nombre):
    """Devuelve todas las llamadas de nivel superior Nombre(...) del texto, ya convertidas."""
    out = []; i = 0
    while True:
        m = re.compile(r"(?<!class )(?<![\w.])%s\(" % re.escape(nombre)).search(texto, i)
        if not m: return out
        p = P(texto); p.i = m.start()
        out.append(p.value()); i = p.i

def limpiar_consts(x):
    if isinstance(x, dict):
        if set(x) == {"_const"}: return x["_const"].split(".")[-1]
        return {k: limpiar_consts(v) for k, v in x.items() if k != "_tipo"}
    if isinstance(x, list): return [limpiar_consts(v) for v in x]
    return x
