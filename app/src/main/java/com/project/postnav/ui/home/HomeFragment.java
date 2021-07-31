package com.project.postnav.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.WriterException;
import com.project.postnav.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {
    String primaryKey, uniqueId,UState,UDist;
    private HomeViewModel homeViewModel;
    Spinner spinner, statespinner, districtspinner;
    EditText from, to, weight, pincode;
    Button add;
    String typestr = null, fromstr = null, tostr = null, statestr = null, districtstr = null, weightstr = null, batchstr = null, pincodestr = null, statusstr = null;
    ImageView qrImage;
    int uniueId = 5, intValue;

    // for Toast message
    int duration = Toast.LENGTH_SHORT;
    // abbreviations for states in primary key
    String abbreviation[] = {"AP", "AR", "AS", "BR", "CG", "GA", "GJ", "HR", "HP", "JH", "KA", "KL", "MP", "MH",
            "MN", "ML", "MZ", "NL", "OD", "PB", "RJ", "SK", "TN", "TS", "TR", "UP", "UK", "WB", "AN", "CH", "DNH", "DD",
            "DL", "JK", "LA", "LD", "PY"};
    int index = 0, distindex = 0;
    String abvInPrimaryKey;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        // final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //       textView.setText(s);
            }
        });

        // <hooks>
        spinner = root.findViewById(R.id.spinner_id);
        from = root.findViewById(R.id.from_id);
        to = root.findViewById(R.id.to_id);
        weight = root.findViewById(R.id.weight_id);
        pincode = root.findViewById(R.id.pincode_id);
        add = root.findViewById(R.id.btn_id);
        statespinner = root.findViewById(R.id.id_statespinner);
        districtspinner = root.findViewById(R.id.id_districtspinner);
        qrImage = root.findViewById(R.id.qr_id);
        // < / hooks>

        // <dropdown menu for type of packet>
        final ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.Type_dropdown, android.R.layout.simple_spinner_dropdown_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).equals("Letter")) {
                    typestr = "Letter";
                } else if (parent.getItemAtPosition(position).equals("Parcel")) {
                    typestr = "Parcel";
                } else if (parent.getItemAtPosition(position).equals("Envelope")) {
                    typestr = "Envelope";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // < / dropdown menu for type of packet>
        // <dropdown for states>
        final ArrayAdapter<CharSequence> statesadapter = ArrayAdapter.createFromResource(requireContext(), R.array.State_dropdown, android.R.layout.simple_spinner_dropdown_item);
        statesadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statespinner.setAdapter(statesadapter);

        statespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                statestr = parent.getItemAtPosition(position).toString();
                index = parent.getSelectedItemPosition() - 1;

                if (index == -1) {
                } else {
                    abvInPrimaryKey = abbreviation[index];
                }
                // < dropdown for districts >
                if (statestr != null) {

                    if (statestr.equals("-Select receivers State-")) {
                        final ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.Select, android.R.layout.simple_spinner_dropdown_item);
                        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        districtspinner.setAdapter(districtAdapter);

                        districtspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                districtstr = parent.getItemAtPosition(position).toString();
                                distindex = parent.getSelectedItemPosition() - 1;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (statestr.equals("Maharashtra")) {
                        final ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.State_Maharashtra, android.R.layout.simple_spinner_dropdown_item);
                        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        districtspinner.setAdapter(districtAdapter);

                        districtspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                districtstr = parent.getItemAtPosition(position).toString();
                                distindex = parent.getSelectedItemPosition() - 1;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (statestr.equals("Goa")) {
                        final ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.State_Goa, android.R.layout.simple_spinner_dropdown_item);
                        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        districtspinner.setAdapter(districtAdapter);

                        districtspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                districtstr = parent.getItemAtPosition(position).toString();
                                distindex = parent.getSelectedItemPosition() - 1;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (statestr.equals("Andhra Pradesh")) {
                        final ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.State_Andhra_Pradesh, android.R.layout.simple_spinner_dropdown_item);
                        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        districtspinner.setAdapter(districtAdapter);

                        districtspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                districtstr = parent.getItemAtPosition(position).toString();
                                distindex = parent.getSelectedItemPosition() - 1;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (statestr.equals("Gujarat")) {
                        final ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.State_Gujarat, android.R.layout.simple_spinner_dropdown_item);
                        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        districtspinner.setAdapter(districtAdapter);

                        districtspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                districtstr = parent.getItemAtPosition(position).toString();
                                distindex = parent.getSelectedItemPosition() - 1;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (statestr.equals("Arunachal Pradesh")) {
                        final ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.State_Arunachal_Pradesh, android.R.layout.simple_spinner_dropdown_item);
                        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        districtspinner.setAdapter(districtAdapter);

                        districtspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                districtstr = parent.getItemAtPosition(position).toString();
                                distindex = parent.getSelectedItemPosition() - 1;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (statestr.equals("Assam")) {
                        final ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.State_Assam, android.R.layout.simple_spinner_dropdown_item);
                        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        districtspinner.setAdapter(districtAdapter);

                        districtspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                districtstr = parent.getItemAtPosition(position).toString();
                                distindex = parent.getSelectedItemPosition() - 1;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (statestr.equals("Bihar")) {
                        final ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.State_Bihar, android.R.layout.simple_spinner_dropdown_item);
                        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        districtspinner.setAdapter(districtAdapter);

                        districtspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                districtstr = parent.getItemAtPosition(position).toString();
                                distindex = parent.getSelectedItemPosition() - 1;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (statestr.equals("Chhattisgarh")) {
                        final ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.State_Chhattisgarh, android.R.layout.simple_spinner_dropdown_item);
                        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        districtspinner.setAdapter(districtAdapter);

                        districtspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                districtstr = parent.getItemAtPosition(position).toString();
                                distindex = parent.getSelectedItemPosition() - 1;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (statestr.equals("Haryana")) {
                        final ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.State_Haryana, android.R.layout.simple_spinner_dropdown_item);
                        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        districtspinner.setAdapter(districtAdapter);

                        districtspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                districtstr = parent.getItemAtPosition(position).toString();
                                distindex = parent.getSelectedItemPosition() - 1;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (statestr.equals("Himachal Pradesh")) {
                        final ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.State_Himachal_Pradesh, android.R.layout.simple_spinner_dropdown_item);
                        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        districtspinner.setAdapter(districtAdapter);

                        districtspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                districtstr = parent.getItemAtPosition(position).toString();
                                distindex = parent.getSelectedItemPosition() - 1;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (statestr.equals("Jharkhand")) {
                        final ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.State_Jharkhand, android.R.layout.simple_spinner_dropdown_item);
                        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        districtspinner.setAdapter(districtAdapter);

                        districtspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                districtstr = parent.getItemAtPosition(position).toString();
                                distindex = parent.getSelectedItemPosition() - 1;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (statestr.equals("Karnataka")) {
                        final ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.State_Karnataka, android.R.layout.simple_spinner_dropdown_item);
                        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        districtspinner.setAdapter(districtAdapter);

                        districtspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                districtstr = parent.getItemAtPosition(position).toString();
                                distindex = parent.getSelectedItemPosition() - 1;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (statestr.equals("Kerala")) {
                        final ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.State_Kerala, android.R.layout.simple_spinner_dropdown_item);
                        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        districtspinner.setAdapter(districtAdapter);

                        districtspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                districtstr = parent.getItemAtPosition(position).toString();
                                distindex = parent.getSelectedItemPosition() - 1;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (statestr.equals("Madhya Pradesh")) {
                        final ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.State_Madhya_Pradesh, android.R.layout.simple_spinner_dropdown_item);
                        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        districtspinner.setAdapter(districtAdapter);

                        districtspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                districtstr = parent.getItemAtPosition(position).toString();
                                distindex = parent.getSelectedItemPosition() - 1;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (statestr.equals("Manipur")) {
                        final ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.State_Manipur, android.R.layout.simple_spinner_dropdown_item);
                        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        districtspinner.setAdapter(districtAdapter);

                        districtspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                districtstr = parent.getItemAtPosition(position).toString();
                                distindex = parent.getSelectedItemPosition() - 1;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (statestr.equals("Meghalaya")) {
                        final ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.State_Manipur, android.R.layout.simple_spinner_dropdown_item);
                        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        districtspinner.setAdapter(districtAdapter);

                        districtspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                districtstr = parent.getItemAtPosition(position).toString();
                                distindex = parent.getSelectedItemPosition() - 1;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (statestr.equals("Mizoram")) {
                        final ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.State_Mizoram, android.R.layout.simple_spinner_dropdown_item);
                        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        districtspinner.setAdapter(districtAdapter);

                        districtspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                districtstr = parent.getItemAtPosition(position).toString();
                                distindex = parent.getSelectedItemPosition() - 1;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (statestr.equals("Nagaland")) {
                        final ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.State_Nagaland, android.R.layout.simple_spinner_dropdown_item);
                        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        districtspinner.setAdapter(districtAdapter);

                        districtspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                districtstr = parent.getItemAtPosition(position).toString();
                                distindex = parent.getSelectedItemPosition() - 1;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (statestr.equals("Odisha")) {
                        final ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.State_Odisha, android.R.layout.simple_spinner_dropdown_item);
                        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        districtspinner.setAdapter(districtAdapter);

                        districtspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                districtstr = parent.getItemAtPosition(position).toString();
                                distindex = parent.getSelectedItemPosition() - 1;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (statestr.equals("Punjab")) {
                        final ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.State_Punjab, android.R.layout.simple_spinner_dropdown_item);
                        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        districtspinner.setAdapter(districtAdapter);

                        districtspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                districtstr = parent.getItemAtPosition(position).toString();
                                distindex = parent.getSelectedItemPosition() - 1;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (statestr.equals("Rajasthan")) {
                        final ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.State_Rajasthan, android.R.layout.simple_spinner_dropdown_item);
                        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        districtspinner.setAdapter(districtAdapter);

                        districtspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                districtstr = parent.getItemAtPosition(position).toString();
                                distindex = parent.getSelectedItemPosition() - 1;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (statestr.equals("Sikkim")) {
                        final ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.State_Sikkim, android.R.layout.simple_spinner_dropdown_item);
                        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        districtspinner.setAdapter(districtAdapter);

                        districtspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                districtstr = parent.getItemAtPosition(position).toString();
                                distindex = parent.getSelectedItemPosition() - 1;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (statestr.equals("Tamil Nadu")) {
                        final ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.State_Tamil_Nadu, android.R.layout.simple_spinner_dropdown_item);
                        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        districtspinner.setAdapter(districtAdapter);

                        districtspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                districtstr = parent.getItemAtPosition(position).toString();
                                distindex = parent.getSelectedItemPosition() - 1;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (statestr.equals("Telangana")) {
                        final ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.State_Telangana, android.R.layout.simple_spinner_dropdown_item);
                        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        districtspinner.setAdapter(districtAdapter);

                        districtspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                districtstr = parent.getItemAtPosition(position).toString();
                                distindex = parent.getSelectedItemPosition() - 1;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (statestr.equals("Tripura")) {
                        final ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.State_Tripura, android.R.layout.simple_spinner_dropdown_item);
                        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        districtspinner.setAdapter(districtAdapter);

                        districtspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                districtstr = parent.getItemAtPosition(position).toString();
                                distindex = parent.getSelectedItemPosition() - 1;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (statestr.equals("Uttar Pradesh")) {
                        final ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.State_Uttar_Pradesh, android.R.layout.simple_spinner_dropdown_item);
                        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        districtspinner.setAdapter(districtAdapter);

                        districtspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                districtstr = parent.getItemAtPosition(position).toString();
                                distindex = parent.getSelectedItemPosition() - 1;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (statestr.equals("Uttarakhand")) {
                        final ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.State_Uttarakhand, android.R.layout.simple_spinner_dropdown_item);
                        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        districtspinner.setAdapter(districtAdapter);

                        districtspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                districtstr = parent.getItemAtPosition(position).toString();
                                distindex = parent.getSelectedItemPosition() - 1;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (statestr.equals("West Bengal")) {
                        final ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.State_West_Bengal, android.R.layout.simple_spinner_dropdown_item);
                        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        districtspinner.setAdapter(districtAdapter);

                        districtspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                districtstr = parent.getItemAtPosition(position).toString();
                                distindex = parent.getSelectedItemPosition() - 1;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (statestr.equals("Andaman and Nicobar Islands")) {
                        final ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.UT_Andaman_and_Nicobar_Islands, android.R.layout.simple_spinner_dropdown_item);
                        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        districtspinner.setAdapter(districtAdapter);

                        districtspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                districtstr = parent.getItemAtPosition(position).toString();
                                distindex = parent.getSelectedItemPosition() - 1;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (statestr.equals("Chandigarh")) {
                        final ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.UT_Chandigarh, android.R.layout.simple_spinner_dropdown_item);
                        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        districtspinner.setAdapter(districtAdapter);

                        districtspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                districtstr = parent.getItemAtPosition(position).toString();
                                distindex = parent.getSelectedItemPosition() - 1;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (statestr.equals("Dadra and Nagar Haveli")) {
                        final ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.UT_Dadra_and_Nagar_Haveli, android.R.layout.simple_spinner_dropdown_item);
                        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        districtspinner.setAdapter(districtAdapter);

                        districtspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                districtstr = parent.getItemAtPosition(position).toString();
                                distindex = parent.getSelectedItemPosition() - 1;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (statestr.equals("Daman and Diu")) {
                        final ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.UT_Daman_and_Diu, android.R.layout.simple_spinner_dropdown_item);
                        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        districtspinner.setAdapter(districtAdapter);

                        districtspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                districtstr = parent.getItemAtPosition(position).toString();
                                distindex = parent.getSelectedItemPosition() - 1;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (statestr.equals("Delhi")) {
                        final ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.UT_Delhi, android.R.layout.simple_spinner_dropdown_item);
                        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        districtspinner.setAdapter(districtAdapter);

                        districtspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                districtstr = parent.getItemAtPosition(position).toString();
                                distindex = parent.getSelectedItemPosition() - 1;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (statestr.equals("Jammu and Kashmir")) {
                        final ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.UT_Jammu_and_Kashmir, android.R.layout.simple_spinner_dropdown_item);
                        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        districtspinner.setAdapter(districtAdapter);

                        districtspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                districtstr = parent.getItemAtPosition(position).toString();
                                distindex = parent.getSelectedItemPosition() - 1;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (statestr.equals("Ladakh")) {
                        final ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.UT_Ladakh, android.R.layout.simple_spinner_dropdown_item);
                        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        districtspinner.setAdapter(districtAdapter);

                        districtspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                districtstr = parent.getItemAtPosition(position).toString();
                                distindex = parent.getSelectedItemPosition() - 1;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (statestr.equals("Lakshadweep")) {
                        final ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.UT_Lakshadweep, android.R.layout.simple_spinner_dropdown_item);
                        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        districtspinner.setAdapter(districtAdapter);

                        districtspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                districtstr = parent.getItemAtPosition(position).toString();
                                distindex = parent.getSelectedItemPosition() - 1;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (statestr.equals("Puducherry")) {
                        final ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.UT_Puducherry, android.R.layout.simple_spinner_dropdown_item);
                        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        districtspinner.setAdapter(districtAdapter);

                        districtspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                districtstr = parent.getItemAtPosition(position).toString();
                                distindex = parent.getSelectedItemPosition() - 1;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }
                }
                // < /dropdown for districts >
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }


        });
        // </dropdown for states>


// < button code>
        // <getting values from editText>
        add.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View v) {

                                       // < getting UID from database>
                                       FirebaseDatabase database = FirebaseDatabase.getInstance();
                                       final DatabaseReference myRef = database.getReference("UID");

                                       myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                           @Override
                                           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                               uniqueId = dataSnapshot.getValue().toString();
                                               intValue = Integer.parseInt(uniqueId) + 1;
                                               String value = Integer.toString(intValue);
                                               myRef.setValue(value);
                                               uniueId = intValue;
                                               Log.d("INTVALUE: ", "" + intValue);

                                               fromstr = from.getText().toString().trim();
                                               tostr = to.getText().toString().trim();
                                               weightstr = weight.getText().toString().trim();
                                               pincodestr = pincode.getText().toString().trim();
                                               statusstr = "Arrived";
                                               if(!pincodestr.equals(""))
                                               {
                                                   batchstr = pincodestr.substring(2);
                                               }
                                               Log.d("Batch string : ",batchstr+"");

                                               // No field can be empty
                                               Log.d("TYPE",""+statestr+" "+districtstr+" "+typestr);
                                               if (fromstr.equals("") || tostr.equals("") || statestr.equals("") || districtstr.equals("")
                                                       || weightstr.equals("") || pincodestr.equals("")  || typestr==null
                                                       || typestr.equals("-Select-") || statestr.equals("-Select receivers State-") || districtstr.equals("-Select receivers District-")) {
                                                   if (fromstr.equals("")) {
                                                       Toast.makeText(getContext(), " 'From field can't be empty' ", duration).show();
                                                   } else if (tostr.equals("")) {
                                                       Toast.makeText(getContext(), " 'To' field can't be empty!", duration).show();
                                                   } else if (statestr.equals("") || statestr.equals("-Select receivers State-")) {
                                                       Toast.makeText(getContext(), " 'State' field can't be empty!", duration).show();
                                                   } else if (districtstr.equals("") || districtstr.equals("-Select receivers District-")) {
                                                       Toast.makeText(getContext(), " 'District' field can't be empty!", duration).show();
                                                   } else if (pincodestr.equals("")) {
                                                       Toast.makeText(getContext(), " 'Pincode' can't be empty!", duration).show();
                                                   } else if (weightstr.equals("")) {
                                                       Toast.makeText(getContext(), " 'Weight' in grams can't be empty! ", duration).show();
                                                   } else if (typestr == null || typestr.equals("-Select-")) {
                                                       Toast.makeText(getContext(), " 'Type field can't be empty! ", duration).show();
                                                   }

                                               } else {
                                                   DatabaseReference rootNode = FirebaseDatabase.getInstance().getReference("Packages");
                                                   String dist;
                                                   if (distindex < 9) {
                                                       dist = "0" + (distindex+1);
                                                   } else {
                                                       dist = "" + (distindex+1);
                                                   }

                                                   rootNode.child(statestr).child(districtstr).child(abvInPrimaryKey + dist + batchstr + uniueId).child("From").setValue(fromstr);
                                                   rootNode.child(statestr).child(districtstr).child(abvInPrimaryKey + dist + batchstr + uniueId).child("To").setValue(tostr);
                                                   rootNode.child(statestr).child(districtstr).child(abvInPrimaryKey + dist + batchstr + uniueId).child("State").setValue(statestr);
                                                   rootNode.child(statestr).child(districtstr).child(abvInPrimaryKey + dist + batchstr + uniueId).child("District").setValue(districtstr);
                                                   rootNode.child(statestr).child(districtstr).child(abvInPrimaryKey + dist + batchstr + uniueId).child("Batch").setValue(batchstr);
                                                   rootNode.child(statestr).child(districtstr).child(abvInPrimaryKey + dist + batchstr + uniueId).child("Weight").setValue(weightstr);
                                                   rootNode.child(statestr).child(districtstr).child(abvInPrimaryKey + dist + batchstr + uniueId).child("Pincode").setValue(pincodestr);
                                                   rootNode.child(statestr).child(districtstr).child(abvInPrimaryKey + dist + batchstr + uniueId).child("Status").setValue(statusstr);
                                                   rootNode.child(statestr).child(districtstr).child(abvInPrimaryKey + dist + batchstr + uniueId).child("Type").setValue(typestr);
                                                   rootNode.child(statestr).child(districtstr).child(abvInPrimaryKey + dist + batchstr + uniueId).child("CurrentLocation").setValue(UDist+", "+UState);

                                                   /**/
                                                   addTimestamp(rootNode.child(statestr).child(districtstr).child(abvInPrimaryKey + dist + batchstr + uniueId));
                                                   /**/
                                                   // Toast message
                                                   Toast.makeText(getContext(), "Package information added successfully", duration).show();
                                                   // QR code

                                                   WindowManager manager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
                                                   String inputValue = abvInPrimaryKey + dist + batchstr + uniueId;
                                                   Display display = manager.getDefaultDisplay();
                                                   Point point = new Point();
                                                   display.getSize(point);
                                                   int width = point.x;
                                                   int height = point.y;
                                                   int smallerDimension = width < height ? width : height;
                                                   smallerDimension = smallerDimension * 3 / 4;
                                                   QRGEncoder qrgEncoder = new QRGEncoder(inputValue, null, QRGContents.Type.TEXT, smallerDimension);
                                                   Bitmap bitmap = null;
                                                   try {
                                                       // Getting QR-Code as Bitmap
                                                       bitmap = qrgEncoder.encodeAsBitmap();
                                                       // Setting Bitmap to ImageView
                                                       qrImage.setImageBitmap(bitmap);
                                                   } catch (WriterException e) {
                                                       Log.d("Exception", e.toString());
                                                   }

                                                   // saving qr code
                                                   String savePath = Environment.getExternalStorageDirectory().getPath()  + "/QRCode/";
                                                   if(bitmap!=null)
                                                   {

                                                       try {
//                                                           QRGSaver.save(savePath, inputValue, bitmap, QRGContents.ImageType.IMAGE_JPEG);
//                                                           Log.d("Path path: ",Environment.getExternalStorageDirectory().getPath()  + "/QRCode/");
                                                           String ImagePath = MediaStore.Images.Media.insertImage(
                                                                   getActivity().getContentResolver(),
                                                                   bitmap,
                                                                   inputValue,
                                                                   "demo_image"
                                                           );

                                                           Uri  URI  = Uri.parse(ImagePath);
                                                           Toast.makeText(getContext(),"Image saved successfully",Toast.LENGTH_LONG).show();


                                                       } catch (Exception e) {
                                                           e.printStackTrace();
                                                       }
                                                   }
                                               }

                                           }

                                           @Override
                                           public void onCancelled(@NonNull DatabaseError databaseError) {

                                           }
                                       });
                                       // < / getting UID from database>


                                   }
                                   //}
                               }
        );
        // </getting values from editText>


// < /button code>
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        DatabaseReference UserRef = FirebaseDatabase.getInstance().getReference().child("Employees")
                .child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace('.','_'));
        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UDist = snapshot.child("District").getValue(String.class);
                UState = snapshot.child("State").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addTimestamp(DatabaseReference ref) {
        DatabaseReference reference = ref.child("Timestamp").child("TS001");
        reference.child("Time").setValue(new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date()));
        reference.child("Date").setValue(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date()));
        reference.child("Location").setValue(UDist+", "+UState);
        reference.child("Remark").setValue("Package Arrived.");
    }
}