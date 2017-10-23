package ru.qualitylab.evotor.evotortest6;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.evotor.devices.commons.printer.printable.IPrintable;
import ru.evotor.devices.commons.printer.printable.PrintableBarcode;
import ru.evotor.devices.commons.printer.printable.PrintableImage;
import ru.evotor.devices.commons.printer.printable.PrintableText;
import ru.evotor.framework.core.IntegrationService;
import ru.evotor.framework.core.action.event.receipt.changes.receipt.print_extra.SetPrintExtra;
import ru.evotor.framework.core.action.event.receipt.print_extra.PrintExtraRequiredEvent;
import ru.evotor.framework.core.action.event.receipt.print_extra.PrintExtraRequiredEventProcessor;
import ru.evotor.framework.core.action.event.receipt.print_extra.PrintExtraRequiredEventResult;
import ru.evotor.framework.core.action.processor.ActionProcessor;
import ru.evotor.framework.receipt.ExtraKey;
import ru.evotor.framework.receipt.Position;
import ru.evotor.framework.receipt.Receipt;
import ru.evotor.framework.receipt.ReceiptApi;
import ru.evotor.framework.receipt.print_extras.PrintExtraPlacePositionAllSubpositionsFooter;
import ru.evotor.framework.receipt.print_extras.PrintExtraPlacePositionFooter;
import ru.evotor.framework.receipt.print_extras.PrintExtraPlacePrintGroupHeader;
import ru.evotor.framework.receipt.print_extras.PrintExtraPlacePrintGroupSummary;
import ru.evotor.framework.receipt.print_extras.PrintExtraPlacePrintGroupTop;

public class MyPrintPaybackService extends IntegrationService {

    private Bitmap getBitmapFromAsset(String strName) {
        AssetManager assetManager = getAssets();
        InputStream istr = null;
        try {
            istr = assetManager.open(strName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeStream(istr);
    }

    @Nullable
    @Override
    protected Map<String, ActionProcessor> createProcessors() {
        Map<String, ActionProcessor> map = new HashMap<>();
        map.put(
                PrintExtraRequiredEvent.NAME_PAYBACK_RECEIPT,
                new PrintExtraRequiredEventProcessor() {
                    @Override
                    public void call(@NotNull String s, @NotNull PrintExtraRequiredEvent printExtraRequiredEvent, @NotNull Callback callback) {
                        List<SetPrintExtra> setPrintExtras = new ArrayList<>();
                        setPrintExtras.add(new SetPrintExtra(
                                new PrintExtraPlacePrintGroupTop(null),
                                new IPrintable[]{
                                        new PrintableText("Proin eget tortor risus. Nulla quis lorem ut libero malesuada feugiat. Proin eget tortor risus."),
                                        new PrintableImage(getBitmapFromAsset("ic_launcher.png"))
                                }
                        ));
                        setPrintExtras.add(new SetPrintExtra(
                                new PrintExtraPlacePrintGroupHeader(null),
                                new IPrintable[]{
                                        new PrintableBarcode("4750232005910", PrintableBarcode.BarcodeType.EAN13),
                                        new PrintableText("Proin eget tortor risus. Nulla quis lorem ut libero malesuada feugiat. Proin eget tortor risus.")
                                }
                        ));
                        Receipt r = ReceiptApi.getReceipt(MyPrintPaybackService.this, Receipt.Type.PAYBACK);
                        if (r != null) {
                            setPrintExtras.add(new SetPrintExtra(
                                    new PrintExtraPlacePrintGroupSummary(null),
                                    new IPrintable[]{
                                            new PrintableText("Proin eget tortor risus. Nulla quis lorem ut libero malesuada feugiat. Proin eget tortor risus.")
                                    }
                            ));
                            for (Position p : r.getPositions()) {
                                List<ExtraKey> list = new ArrayList<>(p.getExtraKeys());
                                setPrintExtras.add(new SetPrintExtra(
                                        new PrintExtraPlacePositionFooter(p.getUuid()),
                                        new IPrintable[]{
                                                new PrintableText("UUID:" + p.getUuid() + "\n EXTRA:" + (list.size() > 0 ? list.get(0).getDescription() : ""))
                                        }
                                ));
                            setPrintExtras.add(new SetPrintExtra(
                                    new PrintExtraPlacePositionAllSubpositionsFooter(p.getUuid()),
                                    new IPrintable[]{
                                            new PrintableText("<Текст>\n" + p.getUuid() + "\n<Текст>")
                                    }
                            ));

                            }
                        }
                        try {
                            callback.onResult(new PrintExtraRequiredEventResult(setPrintExtras).toBundle());
                        } catch (RemoteException exc) {
                            exc.printStackTrace();
                        }
                    }
                }
        );
        return map;
    }
}
