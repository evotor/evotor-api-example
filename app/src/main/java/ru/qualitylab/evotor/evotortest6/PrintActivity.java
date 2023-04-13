package ru.qualitylab.evotor.evotortest6;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.annotation.IdRes;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;

import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import ru.evotor.devices.commons.ConnectionWrapper;
import ru.evotor.devices.commons.DeviceServiceConnector;
import ru.evotor.devices.commons.exception.DeviceServiceException;
import ru.evotor.devices.commons.printer.PrinterDocument;
import ru.evotor.devices.commons.printer.printable.IPrintable;
import ru.evotor.devices.commons.printer.printable.PrintableBarcode;
import ru.evotor.devices.commons.printer.printable.PrintableImage;
import ru.evotor.devices.commons.printer.printable.PrintableText;
import ru.evotor.devices.commons.services.IPrinterServiceWrapper;
import ru.evotor.devices.commons.services.IScalesServiceWrapper;
import ru.evotor.framework.core.IntegrationAppCompatActivity;
import ru.evotor.integrations.BarcodeBroadcastReceiver;

/**
 * Пример работы с принтером чеков терминала
 * В манифесте добавить права <uses-permission android:name="ru.evotor.permission.receipt.printExtra.SET" />
 */
public class PrintActivity extends IntegrationAppCompatActivity {

    EditText etText;
    RadioGroup rgType, rgBarType;
    ListView list;

    //Тип штрихкода: EAN13, EAN8, CODE39, UPCA
    PrintableBarcode.BarcodeType barType = PrintableBarcode.BarcodeType.EAN8;
    //Тип печати: BARCODE, IMAGE, TEXT
    PrintData.PrintType printType = PrintData.PrintType.TEXT;

    List<PrintData> mList = new ArrayList<>();
    PrintAdapter adapter;

    BarcodeBroadcastReceiver mBarcodeBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);

        etText = (EditText) findViewById(R.id.etText);
        rgBarType = (RadioGroup) findViewById(R.id.rgBarType);
        rgType = (RadioGroup) findViewById(R.id.rgType);
        list = (ListView) findViewById(R.id.list);

        //Инициализация оборудования
        DeviceServiceConnector.startInitConnections(getApplicationContext());
        DeviceServiceConnector.addConnectionWrapper(new ConnectionWrapper() {
            @Override
            public void onPrinterServiceConnected(IPrinterServiceWrapper printerService) {
                Log.e(getClass().getSimpleName(), "onPrinterServiceConnected");
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            //Печать сообщения об успешной инициализации принтера
                            DeviceServiceConnector.getPrinterService().printDocument(
                                    //В настоящий момент печать возможна только на ККМ, встроенной в смарт-терминал,
                                    //поэтому вместо номера устройства всегда следует передавать константу
                                    ru.evotor.devices.commons.Constants.DEFAULT_DEVICE_INDEX,
                                    new PrinterDocument(
                                            new PrintableText("PRINTER INIT OK")));
                        } catch (DeviceServiceException e) {
                            e.printStackTrace();
                        }

                    }
                }.start();
            }

            @Override
            public void onPrinterServiceDisconnected() {
                Log.e(getClass().getSimpleName(), "onPrinterServiceDisconnected");
            }

            @Override
            public void onScalesServiceConnected(IScalesServiceWrapper scalesService) {
                Log.e(getClass().getSimpleName(), "onScalesServiceConnected");
            }

            @Override
            public void onScalesServiceDisconnected() {
                Log.e(getClass().getSimpleName(), "onScalesServiceDisconnected");
            }
        });

        rgBarType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i) {
                    case -1:
                        break;
                    case R.id.rbEan8:
                        barType = PrintableBarcode.BarcodeType.EAN8;
                        break;
                    case R.id.rbEan13:
                        barType = PrintableBarcode.BarcodeType.EAN13;
                        break;
                    case R.id.rbCode39:
                        barType = PrintableBarcode.BarcodeType.CODE39;
                        break;
                    case R.id.rbUpca:
                        barType = PrintableBarcode.BarcodeType.UPCA;
                        break;
                    default:
                        break;
                }
            }
        });

        rgType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i) {
                    case -1:
                        break;
                    case R.id.rbText:
                        printType = PrintData.PrintType.TEXT;
                        break;
                    case R.id.rbBarCode:
                        printType = PrintData.PrintType.BARCODE;
                        break;
                    case R.id.rbImage:
                        printType = PrintData.PrintType.IMAGE;
                        break;
                    default:
                        break;
                }
            }
        });

        findViewById(R.id.btnLorem).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Генерация случайных слов
                Lorem lorem = LoremIpsum.getInstance();
                etText.setText(lorem.getWords(10));
            }
        });

        findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Добавить указанные данные для печати в список
                mList.add(new PrintData(barType, printType, etText.getText().toString()));
                adapter = new PrintAdapter(PrintActivity.this, R.layout.customeditrow, mList);
                list.setAdapter(adapter);
            }
        });

        findViewById(R.id.btnPrint).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Печать списка данных на принтере
                if (mList.size() > 0) {
                    final List<IPrintable> pList = new ArrayList<>();
                    for (PrintData item : mList) {
                        if (item.getPrintType().equals(PrintData.PrintType.TEXT)) {
                            //Печать текста
                            pList.add(new PrintableText(item.getData()));
                        } else if (item.getPrintType().equals(PrintData.PrintType.BARCODE)) {
                            //Печать штрихкода
                            //Штрихкод должен быть с верной контрольной суммой
                            pList.add(new PrintableBarcode(item.getData(), item.getBarType()));
                        } else if (item.getPrintType().equals(PrintData.PrintType.IMAGE)) {
                            //Печать картинки
                            pList.add(new PrintableImage(getBitmapFromAsset("ic_launcher.png")));
                        }
                    }
                    new Thread() {
                        @Override
                        public void run() {
                            try {

                                DeviceServiceConnector.getPrinterService().printDocument(
                                        //В настоящий момент печать возможна только на ККМ, встроенной в смарт-терминал,
                                        //поэтому вместо номера устройства всегда следует передавать константу
                                        ru.evotor.devices.commons.Constants.DEFAULT_DEVICE_INDEX,
                                        new PrinterDocument(pList.toArray(new IPrintable[pList.size()])));
                            } catch (DeviceServiceException e) {
                                e.printStackTrace();
                            }

                        }
                    }.start();
                }
            }
        });

        //Получение штрихкода со сканера
        mBarcodeBroadcastReceiver = new BarcodeBroadcastReceiver() {
            @Override
            public void onBarcodeReceived(String barcode, Context context) {
                etText.setText(barcode);
            }
        };

        //Тестовые данные для печати
        //Все штрихкоды с контрольной суммой
        mList.add(new PrintData(barType, printType, "Nulla quis lorem ut libero malesuada feugiat. Nulla porttitor accumsan tincidunt. Curabitur non nulla sit amet nisl tempus convallis quis ac lectus."));
        mList.add(new PrintData(barType, PrintData.PrintType.IMAGE, ""));
        mList.add(new PrintData(PrintableBarcode.BarcodeType.EAN13, PrintData.PrintType.BARCODE, "4750232005910"));
        mList.add(new PrintData(PrintableBarcode.BarcodeType.EAN13, PrintData.PrintType.BARCODE, "4008297322610"));
        mList.add(new PrintData(PrintableBarcode.BarcodeType.EAN13, PrintData.PrintType.BARCODE, "4660015203351"));
        mList.add(new PrintData(PrintableBarcode.BarcodeType.EAN13, PrintData.PrintType.BARCODE, "4605246001970"));
        mList.add(new PrintData(PrintableBarcode.BarcodeType.EAN8, PrintData.PrintType.BARCODE, "46114709"));
        mList.add(new PrintData(PrintableBarcode.BarcodeType.EAN8, PrintData.PrintType.BARCODE, "47502321"));
        mList.add(new PrintData(PrintableBarcode.BarcodeType.EAN8, PrintData.PrintType.BARCODE, "40082974"));
        mList.add(new PrintData(PrintableBarcode.BarcodeType.EAN8, PrintData.PrintType.BARCODE, "46052469"));
        mList.add(new PrintData(PrintableBarcode.BarcodeType.EAN8, PrintData.PrintType.BARCODE, "80325956"));
        mList.add(new PrintData(PrintableBarcode.BarcodeType.EAN8, PrintData.PrintType.BARCODE, "50539901"));
        mList.add(new PrintData(PrintableBarcode.BarcodeType.EAN8, PrintData.PrintType.BARCODE, "36005307"));
        mList.add(new PrintData(PrintableBarcode.BarcodeType.EAN8, PrintData.PrintType.BARCODE, "54100763"));
        mList.add(new PrintData(PrintableBarcode.BarcodeType.EAN8, PrintData.PrintType.BARCODE, "97819035"));
        mList.add(new PrintData(PrintableBarcode.BarcodeType.EAN8, PrintData.PrintType.BARCODE, "59009429"));
        mList.add(new PrintData(PrintableBarcode.BarcodeType.EAN8, PrintData.PrintType.BARCODE, "80005001"));
        mList.add(new PrintData(PrintableBarcode.BarcodeType.EAN8, PrintData.PrintType.BARCODE, "69312809"));
        mList.add(new PrintData(PrintableBarcode.BarcodeType.EAN8, PrintData.PrintType.BARCODE, "40845005"));
        mList.add(new PrintData(PrintableBarcode.BarcodeType.EAN8, PrintData.PrintType.BARCODE, "29040506"));
        mList.add(new PrintData(PrintableBarcode.BarcodeType.EAN8, PrintData.PrintType.BARCODE, "48200080"));
        mList.add(new PrintData(PrintableBarcode.BarcodeType.EAN8, PrintData.PrintType.BARCODE, "50002139"));
        mList.add(new PrintData(PrintableBarcode.BarcodeType.EAN8, PrintData.PrintType.BARCODE, "46700018"));
        mList.add(new PrintData(PrintableBarcode.BarcodeType.EAN13, PrintData.PrintType.BARCODE, "8032595501039"));
        mList.add(new PrintData(PrintableBarcode.BarcodeType.EAN13, PrintData.PrintType.BARCODE, "5053990113057"));
        mList.add(new PrintData(PrintableBarcode.BarcodeType.EAN13, PrintData.PrintType.BARCODE, "3600530815845"));
        mList.add(new PrintData(PrintableBarcode.BarcodeType.EAN13, PrintData.PrintType.BARCODE, "5410076068098"));
        mList.add(new PrintData(PrintableBarcode.BarcodeType.EAN13, PrintData.PrintType.BARCODE, "9781903128947"));
        mList.add(new PrintData(PrintableBarcode.BarcodeType.EAN13, PrintData.PrintType.BARCODE, "5900942351107"));
        mList.add(new PrintData(PrintableBarcode.BarcodeType.EAN13, PrintData.PrintType.BARCODE, "8000500191712"));
        mList.add(new PrintData(PrintableBarcode.BarcodeType.EAN13, PrintData.PrintType.BARCODE, "6931280300709"));
        mList.add(new PrintData(PrintableBarcode.BarcodeType.EAN13, PrintData.PrintType.BARCODE, "4084500569508"));
        mList.add(new PrintData(PrintableBarcode.BarcodeType.EAN13, PrintData.PrintType.BARCODE, "2904050044480"));
        mList.add(new PrintData(PrintableBarcode.BarcodeType.EAN13, PrintData.PrintType.BARCODE, "4820008092434"));
        mList.add(new PrintData(PrintableBarcode.BarcodeType.EAN13, PrintData.PrintType.BARCODE, "5000213014905"));
        mList.add(new PrintData(PrintableBarcode.BarcodeType.EAN13, PrintData.PrintType.BARCODE, "4670001497336"));
        mList.add(new PrintData(PrintableBarcode.BarcodeType.CODE39, PrintData.PrintType.BARCODE, "ABC12345678"));
        mList.add(new PrintData(PrintableBarcode.BarcodeType.UPCA, PrintData.PrintType.BARCODE, "803259550100"));
        mList.add(new PrintData(PrintableBarcode.BarcodeType.UPCA, PrintData.PrintType.BARCODE, "505399011304"));
        mList.add(new PrintData(PrintableBarcode.BarcodeType.UPCA, PrintData.PrintType.BARCODE, "360053081587"));
        mList.add(new PrintData(PrintableBarcode.BarcodeType.UPCA, PrintData.PrintType.BARCODE, "541007606807"));
        mList.add(new PrintData(PrintableBarcode.BarcodeType.UPCA, PrintData.PrintType.BARCODE, "978190312893"));
        mList.add(new PrintData(PrintableBarcode.BarcodeType.UPCA, PrintData.PrintType.BARCODE, "590094235117"));
        mList.add(new PrintData(PrintableBarcode.BarcodeType.UPCA, PrintData.PrintType.BARCODE, "800050019171"));
        mList.add(new PrintData(PrintableBarcode.BarcodeType.UPCA, PrintData.PrintType.BARCODE, "693128030075"));
        mList.add(new PrintData(PrintableBarcode.BarcodeType.UPCA, PrintData.PrintType.BARCODE, "408450056958"));
        mList.add(new PrintData(PrintableBarcode.BarcodeType.UPCA, PrintData.PrintType.BARCODE, "290405004448"));
        mList.add(new PrintData(PrintableBarcode.BarcodeType.UPCA, PrintData.PrintType.BARCODE, "482000809249"));
        mList.add(new PrintData(PrintableBarcode.BarcodeType.UPCA, PrintData.PrintType.BARCODE, "500021301495"));
        mList.add(new PrintData(PrintableBarcode.BarcodeType.UPCA, PrintData.PrintType.BARCODE, "467000149731"));
        mList.add(new PrintData(barType, PrintData.PrintType.TEXT, "Finish!"));

        adapter = new PrintAdapter(this, R.layout.customeditrow, mList);
        list.setAdapter(adapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Останавливаем подписку для событий сканера
        unregisterReceiver(mBarcodeBroadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Регистрируем подписку на события сканера
        registerReceiver(mBarcodeBroadcastReceiver, BarcodeBroadcastReceiver.BARCODE_INTENT_FILTER, BarcodeBroadcastReceiver.SENDER_PERMISSION, null);
    }

    /**
     * Получение картинки из каталога asset приложения
     *
     * @param fileName имя файла
     * @return значение типа Bitmap
     */
    private Bitmap getBitmapFromAsset(String fileName) {
        AssetManager assetManager = getAssets();
        InputStream stream = null;
        try {
            stream = assetManager.open(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeStream(stream);
    }
}
