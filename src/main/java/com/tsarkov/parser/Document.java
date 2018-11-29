package com.tsarkov.parser;

/**
 * Класс предоставляет интерфейс для взаимодействия(ввода-выводв) с пользователем
 */
public class Document {
    static  Node threeNode = new Node(0, null, "", "");
    private SendingToClient sendingToClient;

    /**
     * Метод передает путь к файлу в обрабатывающий класс,
     * возвращает строку
     * инициализирует поле SendingToRecipient
     * @param pathFile Содержит путь к файлу для обработки
     * @return Возвращает строку с результатом парсинга
     */
   public String parseFile(String pathFile) {
       if (pathFile != null) {
           sendingToClient = new SendingToClient();
           String resultJob = new ThreeNodeCreator().newInstanse(pathFile);
           System.out.println(resultJob);
           return resultJob;
       }
       else {
           return "parseFile: Переданный аргумент NULL";
       }
    }

    /**
     * * Метод передает путь к файлу в обрабатывающий класс
     * @param pathLoadFile Содержит путь к файлу для записи результата парсинга
     * @return Возвращает true, если ранее был передан файл для анализа
     */

   public boolean sendResultInFile(String pathLoadFile) {
       if (sendingToClient != null) {
           if(pathLoadFile != null){
           sendingToClient.toFile(pathLoadFile);
           return true;
           } else {
               System.out.println("sendResultInFile: Переданный аргумент NULL");
               return false;
           }
       } else {
           System.out.println("Файл с данными не загружен");
           return false;
       }
   }

    /**
     * Метод для формирования файла с спарсеными данными для последующей загрузки в БД
     * @return Возвращает true, если ранее был передан файл для анализа
     */
   public boolean sendResultInDB(){
       if (sendingToClient != null) {
               sendingToClient.toFileForLiquiBase();
               return true;
           }
        else {
           System.out.println("Файл с данными не загружен");
           return false;
       }
   }
}
