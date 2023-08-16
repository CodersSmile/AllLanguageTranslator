package com.allvoicetranslator.language.translator.ui.camera;

import static android.app.Activity.RESULT_OK;
import static com.allvoicetranslator.language.translator.utils.Const.INPUT;
import static com.allvoicetranslator.language.translator.utils.Const.IN_SELECTED_LANGUAGE;
import static com.allvoicetranslator.language.translator.utils.Const.OUTPUT;
import static com.allvoicetranslator.language.translator.utils.Const.OUT_SELECTED_LANGUAGE;
import static com.allvoicetranslator.language.translator.utils.Const.SELECTED_LANGUAGE;
import static com.allvoicetranslator.language.translator.utils.Const.TEXTEXTRACT;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.allvoicetranslator.language.translator.R;
import com.allvoicetranslator.language.translator.ads.FirebaseADHandlers.AdUtils;
import com.allvoicetranslator.language.translator.databinding.FragmentCameraBinding;
import com.allvoicetranslator.language.translator.ui.activity.DashboardActivity;
import com.allvoicetranslator.language.translator.ui.fragment.LanguageFragment;
import com.allvoicetranslator.language.translator.ui.text.TextFragment;
import com.allvoicetranslator.language.translator.ui.voice.TranslateResultFragment;
import com.allvoicetranslator.language.translator.utils.Const;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;


public class CameraFragment extends Fragment {
    /*    private CameraManager cameraManager;
        private String cameraId;
        private boolean isFlashOn= false;*/
/*    private ExecutorService cameraExecutor;
    private boolean isFlashOn;*/


    FragmentCameraBinding binding;
    DashboardActivity activity;
    CameraSource cameraSource;
    String mText;

    ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            Uri uri = result.getData() != null ? result.getData().getData() : null;
            ImageView mPreviewIv = new ImageView(requireContext());
            mPreviewIv.setImageURI(uri);
            BitmapDrawable bitmapDrawable = (BitmapDrawable) mPreviewIv.getDrawable();
            Bitmap bitmap = bitmapDrawable.getBitmap();

            TextRecognizer recognizer = new TextRecognizer.Builder(activity).build();

            if (!recognizer.isOperational()) {
                Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show();
            } else {
                Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                SparseArray<TextBlock> items = recognizer.detect(frame);
                StringBuilder sb = new StringBuilder();
                //get text from sb until there is no text
                for (int i = 0; i < items.size(); i++) {
                    TextBlock myItem = items.valueAt(i);
                    sb.append(myItem.getValue());
                    String detectedLanguage = myItem.getLanguage();
                    Log.e("TEXTEXTRACT: ", detectedLanguage);

                    sb.append("\n");
                }

                TEXTEXTRACT = sb.toString();
                if (!TEXTEXTRACT.isEmpty()) {
                    AdUtils.showInterstitialAd(activity, state_load -> {
                        activity.openFragment(new TranslateResultFragment(new CameraFragment(), IN_SELECTED_LANGUAGE, OUT_SELECTED_LANGUAGE));
                    });
                } else {
//                    Toast.makeText(activity, "There is no Data..!", Toast.LENGTH_SHORT).show();
                }

            }
        }
    });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_camera, container, false);
        activity = (DashboardActivity) requireActivity();

     /*   if (!hasFlash()) {
            binding.btnFlash.setEnabled(false);
        }*/
      /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && hasFlash()) {
            cameraManager = (CameraManager) getSystemService(requireActivity(), CameraManager.class);
        }
        try {
            cameraId = cameraManager.getCameraIdList()[0]; // Assume the first camera
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }*/
      /*  binding.btnFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFlash();
            }
        });
        cameraExecutor = Executors.newSingleThreadExecutor();*/


        TextRecognizer textRecognizer = new TextRecognizer.Builder(activity).build();

        if (!textRecognizer.isOperational()) {
            Log.w("MainActivity", "Detector dependencies are not yet available");
        } else {
            cameraSource = new CameraSource.Builder(activity, textRecognizer).setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024).setRequestedFps(2.0f)
                    .setAutoFocusEnabled(true).build();
            binding.surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    try {
                        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, 22);
                            return;
                        }
                        cameraSource.start(binding.surfaceView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    cameraSource.stop();
                }
            });

            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {

                }

                @Override
                public void receiveDetections(@NonNull Detector.Detections<TextBlock> detections) {
                    final SparseArray<TextBlock> items = detections.getDetectedItems();
                    if (items.size() != 0) {

                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 0; i < items.size(); i++) {
                            TextBlock item = items.valueAt(i);
                            stringBuilder.append(item.getValue());
                            stringBuilder.append("\n");
                            mText = stringBuilder.toString();
                            String detectedLanguage = item.getLanguage();
                            Log.e("TEXTEXTRACT: ", detectedLanguage);
                        }
                    }
                }
            });
        }

        return binding.getRoot();
    }

 /*   @Override
    public void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }*/

  /*  private boolean hasFlash() {
        return requireActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }
*/
 /*   private void toggleFlash() {
        if (isFlashOn) {
            turnFlashOff();
        } else {
            turnFlashOn();
        }
    }*/


    @Override
    public void onResume() {
        super.onResume();

        AdUtils.loadInitialInterstitialAds(activity);
        AdUtils.loadAppOpenAds(activity);

        AdUtils.loadInitialNativeList(activity);

        binding.btnInput.setText(IN_SELECTED_LANGUAGE.getLanguageName());
        binding.btnOutput.setText(OUT_SELECTED_LANGUAGE.getLanguageName());

        binding.btnSwap.setOnClickListener(view -> {
            SELECTED_LANGUAGE = IN_SELECTED_LANGUAGE;
            IN_SELECTED_LANGUAGE = OUT_SELECTED_LANGUAGE;
            OUT_SELECTED_LANGUAGE = SELECTED_LANGUAGE;

            binding.btnInput.setText(IN_SELECTED_LANGUAGE.getLanguageName());
            binding.btnOutput.setText(OUT_SELECTED_LANGUAGE.getLanguageName());
            SELECTED_LANGUAGE = null;
        });

        binding.btnBack.setOnClickListener(view -> activity.openFirstFragment());

        activity.getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                AdUtils.showInterstitialAd(activity, state_load -> {
                    activity.finish();
                });
            }
        });

        binding.btnSettings.setOnClickListener(view -> {
            AdUtils.showInterstitialAd(activity, state_load -> {
                activity.drawerOpen();
            });
        });

        binding.btnInput.setOnClickListener(view -> {
            AdUtils.showInterstitialAd(activity, state_load -> {
                activity.openFragment(new LanguageFragment(new TextFragment(), INPUT));
            });

        });

        binding.btnOutput.setOnClickListener(view -> {
            AdUtils.showInterstitialAd(activity, state_load -> {
                activity.openFragment(new LanguageFragment(new TextFragment(), OUTPUT));
            });
        });

  /*      binding.btnClick.setOnClickListener(view -> {
            Const.TEXTEXTRACT = mText;
            if (!TEXTEXTRACT.isEmpty()) {
                AdUtils.showInterstitialAd(activity, state_load -> {
                    activity.openFragment(new TranslateResultFragment(new CameraFragment(), IN_SELECTED_LANGUAGE, OUT_SELECTED_LANGUAGE));
                });
            } else {
                Toast.makeText(activity, "There is no Data..!", Toast.LENGTH_SHORT).show();
            }
        });*/
        binding.btnClick.setOnClickListener(view -> {
            Const.TEXTEXTRACT = mText;
            if (!TextUtils.isEmpty(TEXTEXTRACT)) {
                AdUtils.showInterstitialAd(activity, state_load -> {
                    activity.openFragment(new TranslateResultFragment(new CameraFragment(), IN_SELECTED_LANGUAGE, OUT_SELECTED_LANGUAGE));
                });
            } else {
//                Toast.makeText(activity, "There is no Data..!", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnGallery.setOnClickListener(view -> {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 22);
                return;
            }

            ImagePicker.Companion.with(activity)
                    .crop()
                    .galleryOnly()
                    .galleryMimeTypes(new String[]{"image/png", "image/jpg", "image/jpeg"})
                    .maxResultSize(1080, 1920)
                    .createIntent(intent -> {
                        imagePickerLauncher.launch(intent);
                        return null;
                    });
        });

    }

   /* private void toggleFlash() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                if (isFlashOn) {
                    cameraManager.setTorchMode(cameraId, false); // Turn off flash
                    isFlashOn = false;
                } else {
                    cameraManager.setTorchMode(cameraId, true); // Turn on flash
                    isFlashOn = true;
                }
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                if (isFlashOn) {
                    cameraManager.setTorchMode(cameraId, false); // Turn off flash
                    isFlashOn = false;
                } else {
                    cameraManager.setTorchMode(cameraId, true); // Turn on flash
                    isFlashOn = true;
                }
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                if (isFlashOn) {
                    cameraManager.setTorchMode(cameraId, false); // Turn off flash
                    isFlashOn = false;
                } else {
                    cameraManager.setTorchMode(cameraId, true); // Turn on flash
                    isFlashOn = true;
                }
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                if (isFlashOn) {
                    cameraManager.setTorchMode(cameraId, false); // Turn off flash
                    isFlashOn = false;
                } else {
                    cameraManager.setTorchMode(cameraId, true); // Turn on flash
                    isFlashOn = true;
                }
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }
*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 22) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                try {
                    cameraSource.start(binding.surfaceView.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}