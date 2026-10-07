package upb.rutas.persistencia;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Parser/escritor JSON minimalista de propósito específico para este proyecto (RF-08),
 * evita depender de una librería externa. Soporta objetos, arreglos, strings, números,
 * booleanos y null, suficiente para el esquema de nodos/aristas/lugares usado aquí.
 */
public final class JsonLite {

    private JsonLite() {
    }

    public static Object parse(String texto) {
        Parser parser = new Parser(texto);
        Object valor = parser.parseValue();
        parser.skipWhitespace();
        return valor;
    }

    @SuppressWarnings("unchecked")
    public static String write(Object valor) {
        StringBuilder sb = new StringBuilder();
        writeValue(valor, sb, 0);
        return sb.toString();
    }

    private static void writeValue(Object valor, StringBuilder sb, int indent) {
        if (valor instanceof Map<?, ?> mapa) {
            writeObject(mapa, sb, indent);
        } else if (valor instanceof List<?> lista) {
            writeArray(lista, sb, indent);
        } else if (valor instanceof String texto) {
            writeString(texto, sb);
        } else if (valor instanceof Boolean || valor instanceof Number) {
            sb.append(valor);
        } else if (valor == null) {
            sb.append("null");
        } else {
            writeString(valor.toString(), sb);
        }
    }

    private static void writeObject(Map<?, ?> mapa, StringBuilder sb, int indent) {
        if (mapa.isEmpty()) {
            sb.append("{}");
            return;
        }
        sb.append("{\n");
        int i = 0;
        for (Map.Entry<?, ?> entry : mapa.entrySet()) {
            sb.append("  ".repeat(indent + 1));
            writeString(String.valueOf(entry.getKey()), sb);
            sb.append(": ");
            writeValue(entry.getValue(), sb, indent + 1);
            if (++i < mapa.size()) {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append("  ".repeat(indent)).append("}");
    }

    private static void writeArray(List<?> lista, StringBuilder sb, int indent) {
        if (lista.isEmpty()) {
            sb.append("[]");
            return;
        }
        sb.append("[\n");
        for (int i = 0; i < lista.size(); i++) {
            sb.append("  ".repeat(indent + 1));
            writeValue(lista.get(i), sb, indent + 1);
            if (i < lista.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append("  ".repeat(indent)).append("]");
    }

    private static void writeString(String texto, StringBuilder sb) {
        sb.append('"');
        for (char c : texto.toCharArray()) {
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> sb.append(c);
            }
        }
        sb.append('"');
    }

    private static final class Parser {
        private final String texto;
        private int pos;

        private Parser(String texto) {
            this.texto = texto;
            this.pos = 0;
        }

        private void skipWhitespace() {
            while (pos < texto.length() && Character.isWhitespace(texto.charAt(pos))) {
                pos++;
            }
        }

        private char peek() {
            return texto.charAt(pos);
        }

        private Object parseValue() {
            skipWhitespace();
            char c = peek();
            return switch (c) {
                case '{' -> parseObject();
                case '[' -> parseArray();
                case '"' -> parseString();
                case 't', 'f' -> parseBoolean();
                case 'n' -> parseNull();
                default -> parseNumber();
            };
        }

        private Map<String, Object> parseObject() {
            Map<String, Object> mapa = new LinkedHashMap<>();
            pos++; // {
            skipWhitespace();
            if (peek() == '}') {
                pos++;
                return mapa;
            }
            while (true) {
                skipWhitespace();
                String clave = parseString();
                skipWhitespace();
                pos++; // :
                Object valor = parseValue();
                mapa.put(clave, valor);
                skipWhitespace();
                char c = peek();
                pos++;
                if (c == '}') {
                    break;
                }
            }
            return mapa;
        }

        private List<Object> parseArray() {
            List<Object> lista = new ArrayList<>();
            pos++; // [
            skipWhitespace();
            if (peek() == ']') {
                pos++;
                return lista;
            }
            while (true) {
                Object valor = parseValue();
                lista.add(valor);
                skipWhitespace();
                char c = peek();
                pos++;
                if (c == ']') {
                    break;
                }
            }
            return lista;
        }

        private String parseString() {
            pos++; // "
            StringBuilder sb = new StringBuilder();
            while (peek() != '"') {
                char c = texto.charAt(pos);
                if (c == '\\') {
                    pos++;
                    char escape = texto.charAt(pos);
                    switch (escape) {
                        case 'n' -> sb.append('\n');
                        case 'r' -> sb.append('\r');
                        case 't' -> sb.append('\t');
                        case '"' -> sb.append('"');
                        case '\\' -> sb.append('\\');
                        default -> sb.append(escape);
                    }
                } else {
                    sb.append(c);
                }
                pos++;
            }
            pos++; // "
            return sb.toString();
        }

        private Boolean parseBoolean() {
            if (texto.startsWith("true", pos)) {
                pos += 4;
                return Boolean.TRUE;
            }
            pos += 5;
            return Boolean.FALSE;
        }

        private Object parseNull() {
            pos += 4;
            return null;
        }

        private Double parseNumber() {
            int inicio = pos;
            while (pos < texto.length() && "-+.0123456789eE".indexOf(texto.charAt(pos)) >= 0) {
                pos++;
            }
            return Double.parseDouble(texto.substring(inicio, pos));
        }
    }
}
