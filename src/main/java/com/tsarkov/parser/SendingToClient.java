package com.tsarkov.parser;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * Класс для вывода данных построенного дерева нод во внешние носители.
 */
public class SendingToClient {
    /**
     * Метод записывает спарсеные данные в указанный файл
     * @param pathWriteFile Путь к файлу для записи
     */
    void toFile(String pathWriteFile) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(pathWriteFile, false))) {
            readTreeAndWriteOutput(Document.threeNode, writer);
        } catch (IOException e) {
            System.out.println("Файл пуст либо не найден");
            e.printStackTrace();
        }
        System.out.println("Данные успешно загружены в файл");
    }
    /**
     * Метод записывает спарсеные данные в указанный файл
     * для последующей загрузки в БД LiquiBase
     */
    void toFileForLiquiBase(){
        String pathWriteFile =  "./src/main/resources/db/changelog/1/add.sql";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(pathWriteFile, false))) {
            String createTable = "--liquibase add sql\n" +
                    "--changeset tsarkov\n" +
                    "INSERT INTO tree_table (node_id, parent_id, node_name, node_value) VALUES ('0','0','','');\n";
            writer.write(createTable);
            readTreeAndWriteSQLFile(Document.threeNode, writer);
        } catch (IOException e) {
            System.out.println("Файл пуст либо не найден");
            e.printStackTrace();
        }
        System.out.println("Данные успешно загружены в файл");
    }
    /**
     * Метод рекурсивно обходит все узлы созданого дерева нод
     * и записывает их поля в указанный файл
     * @param node Корень дерева
     * @param writer Поток для записи
     * @throws IOException
     */
    private void readTreeAndWriteOutput(Node node, Writer writer) throws IOException {
        writer.write("[ " + node.id + ", " + (node.parent == null ? 0 : node.parent.id) + ", " + node.name + ", " + node.value + " ]\n");
        if (node.children.size() != 0) {
            for (Node nd : node.children) {
                readTreeAndWriteOutput(nd, writer);
            }
        }
    }
    /**
     * Метод рекурсивно обходит все узлы созданого дерева нод
     * и записывает их поля в файл в виде sql запросов
     * @param node Корень дерева
     * @param writer Поток для записи
     * @throws IOException
     */
    private void readTreeAndWriteSQLFile (Node node, Writer writer) throws IOException{
        writer.write("INSERT INTO tree_table (node_id, parent_id, node_name, node_value) VALUES ('" +
                node.id + "','" + (node.parent == null ? 0 : node.parent.id) + "','" + node.name + "','" + node.value + "');\n");
        if (node.children.size() != 0) {
            for (Node nd : node.children) {
                readTreeAndWriteSQLFile(nd, writer);
            }
        }
    }
}
