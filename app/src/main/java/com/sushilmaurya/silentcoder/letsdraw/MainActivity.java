package com.sushilmaurya.silentcoder.letsdraw;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.UUID;

public class MainActivity extends AppCompatActivity implements SizeChooser.SizeChooserListener {

    private static final int MY_PERMISSION_WRITE_EXTERNAL_STORAGE = 0;
    private DrawingView drawingView;
    private ImageButton currPaint;
    LinearLayout paintLayout;
    private boolean eraseChosen;
    private String imageSaved;
    private enum Action{
        SAVE,
        SAVE_AND_SHARE
    }
    private Action action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawingView = (DrawingView) findViewById(R.id.drawing);
        paintLayout = (LinearLayout) findViewById(R.id.paint_colors);
        currPaint = (ImageButton) paintLayout.getChildAt(0);
        imageSaved = null;
        currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
        action = Action.SAVE;
    }

    public void paintClicked(View view){

        if(view!=currPaint){
            drawingView.setErase(false);
            drawingView.setBrushSize((int) drawingView.getLastBrushSize());
            ImageButton imgView = (ImageButton)view;
            String color = view.getTag().toString();
            drawingView.setColor(color);
            imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            currPaint.setImageDrawable(getResources().getDrawable(R.drawable.pallete_box));
            currPaint = (ImageButton)view;
        }
    }

    private void launchSizeChooser(boolean eraseSize) {
        this.eraseChosen = eraseSize;
        SizeChooser sizeChooser = new SizeChooser();
        Bundle bundle = new Bundle();
        bundle.putFloat("size", drawingView.getBrushSize());
        sizeChooser.setArguments(bundle);
        sizeChooser.show(getSupportFragmentManager(), "Size");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSION_WRITE_EXTERNAL_STORAGE) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(),
                        "Successfully permission granted",
                        Toast.LENGTH_LONG).show();
                if (action == Action.SAVE_AND_SHARE) {
                    share();
                    action = Action.SAVE;
                } else {
                    saveImage();
                }
            } else {
                Toast.makeText(getApplicationContext(),
                        "Sorry you won't be able to save the file " +
                                "until you give permission",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void getSize(int size) {
        if(eraseChosen){
            drawingView.setErase(true);
            drawingView.setBrushSize(size);
            drawingView.setLastBrushSize(size);

        }else {
            drawingView.setBrushSize(size);
            drawingView.setLastBrushSize(size);
            drawingView.setErase(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AlertDialog.Builder dialog;
        switch (item.getItemId()){
            case R.id.draw_btn:
                launchSizeChooser(false);
                return true;
            case R.id.erase_btn:
                launchSizeChooser(true);
                return true;
            case R.id.new_btn:
                dialog = new AlertDialog.Builder(this);
                dialog.setTitle("New").setMessage("You will loose progress, Are you sure?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                drawingView.startNew();
                                imageSaved = null;
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).setCancelable(true)
                        .show();
                return true;
            case R.id.save_btn:
                dialog = new AlertDialog.Builder(this);
                dialog.setTitle("Save").setMessage("Are you sure you want to save?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                saveImage();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).setCancelable(true)
                        .show();
                return true;
            case R.id.share_btn:
                share();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void share() {
        if(imageSaved == null){
            int savedStatus = saveImage();
            if(savedStatus == Constants.SAVE_FAILED){
                   Toast.makeText(getApplicationContext(),
                           "Save Failed",
                           Toast.LENGTH_LONG).show();
                   return;
            }else if(savedStatus == Constants.PERMISSION_ASKED){
                action = Action.SAVE_AND_SHARE;
                return;
            }
        }
        ShareHelper shareHelper = new ShareHelper();
        Uri uri = Uri.parse(imageSaved);
        shareHelper.shareOnWhatsapp(this, uri);
        imageSaved = null;
    }

    private int saveImage() {
        int status;
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            drawingView.setDrawingCacheEnabled(true);
            imageSaved = MediaStore.Images.Media.insertImage(
                    getContentResolver(),
                    drawingView.getDrawingCache(),
                    UUID.randomUUID().toString() + ".png",
                    "drawing");

            if (imageSaved != null) {
                Toast.makeText(getApplicationContext(), "Saved Successfully",
                        Toast.LENGTH_SHORT).show();
                status = Constants.SAVED;
            } else {
                Toast.makeText(getApplicationContext(), "Oops! Save failed",
                        Toast.LENGTH_SHORT).show();
                status = Constants.SAVE_FAILED;
            }

            drawingView.destroyDrawingCache();
            return status;
        }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    Toast.makeText(this, "Required Permission for saving images", Toast.LENGTH_SHORT).show();
                }
            }
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSION_WRITE_EXTERNAL_STORAGE);
            status = Constants.PERMISSION_ASKED;
        }
        return status;
    }
}
