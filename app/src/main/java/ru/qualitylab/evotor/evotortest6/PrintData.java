package ru.qualitylab.evotor.evotortest6;

import ru.evotor.devices.commons.printer.printable.PrintableBarcode;

/**
 * Created by power on 19.10.2017.
 */

class PrintData {
    enum PrintType {TEXT, BARCODE, IMAGE}

    private PrintableBarcode.BarcodeType barType;
    private PrintType printType;
    private String data;

    public PrintData() {
    }

    public PrintData(PrintableBarcode.BarcodeType barType, PrintType printType, String data) {
        this.barType = barType;
        this.printType = printType;
        this.data = data;
    }

    public PrintableBarcode.BarcodeType getBarType() {
        return barType;
    }

    public void setBarType(PrintableBarcode.BarcodeType barType) {
        this.barType = barType;
    }

    public PrintType getPrintType() {
        return printType;
    }

    public void setPrintType(PrintType printType) {
        this.printType = printType;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
