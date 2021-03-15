package com.sushilmaurya.silentcoder.letsdraw;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by silentcoder on 15/1/18.
 */

public class SizeChooser extends AppCompatDialogFragment implements SeekBar.OnSeekBarChangeListener {
    private SeekBar seekBar;
    private TextView textView;
    private SizeChooserListener listener;
    private float size;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        size = getArguments().getFloat("size");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();

        View view = layoutInflater.inflate(R.layout.size_chooser, null);
        builder.setView(view);

        seekBar = (SeekBar) view.findViewById(R.id.size_seek_bar);
        textView = (TextView) view.findViewById(R.id.size_text_view);
        seekBar.setMax(100);
        seekBar.setKeyProgressIncrement(1);
        seekBar.setOnSeekBarChangeListener(this);
        seekBar.setProgress((int)size);

        builder.setTitle(R.string.choose_size)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.getSize(Integer.parseInt(textView.getText().toString()));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        return builder.create();
    }

    /**
     * Notification that the progress level has changed. Clients can use the fromUser parameter
     * to distinguish user-initiated changes from those that occurred programmatically.
     *
     * @param seekBar  The SeekBar whose progress has changed
     * @param progress The current progress level. This will be in the range 0..max where max
     *                 was set by {@link ProgressBar#setMax(int)}. (The default value for max is 100.)
     * @param fromUser True if the progress change was initiated by the user.
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        textView.setText(String.valueOf(progress));
    }

    /**
     * Notification that the user has started a touch gesture. Clients may want to use this
     * to disable advancing the seekbar.
     *
     * @param seekBar The SeekBar in which the touch gesture began
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    /**
     * Notification that the user has finished a touch gesture. Clients may want to use this
     * to re-enable advancing the seekbar.
     *
     * @param seekBar The SeekBar in which the touch gesture began
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        listener = (SizeChooserListener) context;

    }

    public interface SizeChooserListener{
        void getSize(int size);
    }
}


