# README #

Исходники для тестов Java API Evotor.

Приветствуется правка и дополнение кода :)

CustomAdapter.java - адаптер для списка товаров в чеке EditActivity.java

EditActivity.java - пример работы с позициями чека продажи, ReceiptAPI. Получение открытого чека продажи и операции над элементами чека (правка, удаление и добавление)

CustomEditObject.java - элемент списка EditActivity.java

GlobalReceiver.java - Получение событий об открытии чека, обновлении базы продуктов или результате изменения чека

InventoryApiActivity.java - примеры работы с InventoryAPI (Рассмотрена работа со сканером штрихкода, Получение штрихкодов товара по его уникальному номеру, 
Поиск товара по уникальному идентификатору, Получить возможные дополнительные поля товара, Получить значения ProductExtra дополнительных полей товара)

MainActivity.java - примеры открытия нового чека продажи и возврата, создание и отправка электронного чека

MyDiscountService.java - пример работы со скидками на чек продажи

MyIntegrationService.java - пример сервиса для реакции на события терминала (добавление определенного товара в чек и вызов SuggestActivity.java)

MyPaymentActivity.java - пример взаимодействия со сторонними платежными системами

MyPaymentService.java - пример службы для работы со сторонними платежными системами

MyPrintPaybackService.java - пример сервиса печати внутри чека возврата

MyPrintService.java - пример сервиса печати внутри чека продажи

PayApiActivity.java - пример работы с PaymentSystem API. Получение списка платежных систем.

PrintActivity.java - пример работы с принтером терминала.

PrintAdapter.java - адаптер для списка в примере печати на принтере.

PrintData.java - элемент списка PrintActivity.java

ReceiptApiActivity.java - примеры работы с ReceiptAPI

SplitGroupService.java - пример разделения чека на несколько печатных групп

SplitService.java - пример разделения чека на несколько платежей

SuggestActivity.java - пример Activity для MyIntegrationService.java. Предлагает операции с товаром при добавлении определенного товара в чек продажи.

UserApiActivity.java - пример работы с UserAPI. 