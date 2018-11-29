package com.tsarkov.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.EmptyStackException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  Класс-строитель дерева нод
 */
public class ThreeNodeCreator {
    /**
     * inText Переменная для хранения текста, предназначенного для парсинга
     * idNode Счетчик количества созданных нод
     * regexpNameNode Корректность имени ноды
     * regexpOneNode Содержит один узел со значением в виде других узлов
     * regexpSomeNode Содержит один узел со значением в виде других узлов и несколько узлов с текстовым значением
     * regexpChildrenNode Парсинг имен и значений узлов
     * regexpNodeNoValue Парсинг имени узла без значения
     * regexpLastNode Содержит один и болле узлов с текстовыми значениями
     */
    private String inText = "";
    private int idNode = 0;
    private Pattern regexpNameNode = Pattern.compile("[a-zA-Z_]\\w*");
    private Pattern regexpOneNode = Pattern.compile("^\\s*\\b[a-zA-Z_][\\w]*\\b\\s+\\=\\s+$");
    private Pattern regexpSomeNode = Pattern.compile("^\\s*(?:\\b[a-zA-Z_][\\w]*\\b\\s+\\=\\s+[\"“][^\\n\"“”]+\\w[\"”]\\s*)*\\s+\\b[a-zA-Z_][\\w]*\\b\\s+\\=\\s+$");
    private Pattern regexpChildrenNode = Pattern.compile("[a-zA-Z_][\\w]*\\s+\\=\\s+[\"“][^\\n\"“”]+[\"”]");
    private Pattern regexpNodeNoValue = Pattern.compile("\\b[^\\n\"”“]*(?=\\s+\\=\\s*(?![\"“])$)");
    private Pattern regexpLastNode = Pattern.compile("^(?:\\s*[a-zA-Z_][\\w]*\\s+\\=\\s+[\"“][^\\n\"“”]+[\"”])+\\s*$");

    /**
     * Метод для запуска чтения файла и его парсинга
     * @param pathFile Путь к файлу для чтения
     * @return Возвращает строку с результатом работы метода
     */
    String newInstanse(String pathFile){
            try {
            readFile(pathFile);
            parseString(inText, Document.threeNode);
                } catch (IllegalArgumentException  e) {
                    return "Неверный формат данных";
                } catch (IOException e) {
                    return "Файл пуст либо не найден";

            }
            return "Файл загружен и разобран на узлы синтаксического дерева";
        }

    /**
     * Метод читает данные из файла и записывает в переменную типа String
     * @param pathReadFile Путь к файлу для чтения
     * @throws IOException
     */
    private void readFile(String pathReadFile ) throws  IOException {
        try (BufferedReader rd = new BufferedReader(new FileReader(pathReadFile))) {
            while (rd.ready()) {
                inText += rd.readLine() + " ";
            }
            if (inText.isEmpty()) throw new IOException();
        }
}

    /**
     * Рекурсивный метод для парсинга текста
     * @param inText Исходный текст для парсинга
     * @param rootNode Ссылка на корень дерева
     * @throws IllegalArgumentException
     */
    private void parseString(String inText, Node rootNode) throws EmptyStackException, IllegalArgumentException {
        int indexStart = 0;
        int indexEnd = 0;
        String buffer = "";
        int counterStack = 0;
        // Поиск открывающих скобок и закрывающих скобок, обозначающих границы следующего рекурсивного вызова
        for (int i = 0; i < inText.length(); i++) {
            if (inText.charAt(i) == '{') {
                if (counterStack == 0) {
                    indexStart = i;
                    // Создание строки-буфера для поиска узлов в текущем методе рекурсивного дерева
                    buffer = inText.substring(indexEnd, i);
                }
                // При положительном поиске добавление в стек
                counterStack++;
            }
            if (inText.charAt(i) == '}') {
                // Поиск конца области для запуска ее в рекурсию
                if (counterStack == 1) {
                    indexEnd = i;
                    // Рекурсионный вызов  с передачей в него найденной области, поиск узлов дерева из буфера
                    parseString(inText.substring(indexStart + 1, indexEnd), createNode(rootNode, buffer));
                    indexEnd++;
                }
                // Удаление из стека
                if (counterStack != 0){
                    counterStack--;
                } else {
                    throw new  IllegalArgumentException();
                }
            }
        }
        // Проверка контракта на соотношения открывающихся и закрывающихся символов
        if (counterStack != 0) throw new IllegalArgumentException();
        // Проверка условия конца рекурсионного спуска, парсинг узлов
        if (indexEnd == 0) {
            createNode(rootNode, inText);
        } else {
            if (indexEnd != inText.length() && !inText.substring(indexEnd, inText.length()).matches("^\\s+$")) {
                createNode(rootNode, inText.substring(indexEnd, inText.length()));
            }
        }
    }

    /**
     * Метода для поиска узлов дерева в тексте
     * @param rootNode Узел, являющийся для данного вызова корнем
     * @param inBuffer Строка текста для анализа
     * @return
     */
    private Node createNode(Node rootNode, String inBuffer) {
        // Один узел в строке 
        Matcher m = regexpOneNode.matcher(inBuffer);
        if (m.matches()) {
            // Парсинг узла 
            m = regexpNameNode.matcher(inBuffer);
            m.find();
            // Обнаружения root узла 
            if (idNode == 0) {
                rootNode.name = inBuffer.substring(m.start(), m.end());
                rootNode.id = ++idNode;
                return rootNode;
            } else {
                Node node = new Node(++idNode, rootNode, inBuffer.substring(m.start(), m.end()), "");
                rootNode.children.add(node);
                return node;
            }
        }
        // Несколько узлов в строке 
        m = regexpSomeNode.matcher(inBuffer);
        if (m.matches()) {
            // Парсинг узлов 
            m = regexpChildrenNode.matcher(inBuffer);
            while (m.find()) {
                String[] arrInBuffer = inBuffer.trim().split("\\s");
                Node node = new Node(++idNode, rootNode, arrInBuffer[0], arrInBuffer[2].replaceAll("[\"\\“\\”]", ""));
                rootNode.children.add(node);
            }
            // Парсинг узла без значения 
            m = regexpNodeNoValue.matcher(inBuffer);
            m.find();
            Node node = new Node(++idNode, rootNode, inBuffer.substring(m.start(), m.end()), "");
            rootNode.children.add(node);
            return node;
        }
        // Поиск узлов в конце рекурсивного спуска 
        m = regexpLastNode.matcher(inBuffer);
        if (m.matches()) {
            m = regexpChildrenNode.matcher(inBuffer);
            while (m.find()) {
                String[] arrInBuffer = inBuffer.trim().split("\\s");
                Node node = new Node(++idNode, rootNode, arrInBuffer[0], arrInBuffer[2].replaceAll("[\"\\“\\”]", ""));
                rootNode.children.add(node);
            }
            return null;
        }
        throw new IllegalArgumentException();
    }
}
