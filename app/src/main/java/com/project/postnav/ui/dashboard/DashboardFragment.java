package com.project.postnav.ui.dashboard;

import android.R.layout;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.postnav.Packet;
import com.project.postnav.R;
import com.project.postnav.RecyclerViewAdapter;

import java.util.ArrayList;

public class DashboardFragment extends androidx.fragment.app.Fragment {

    ArrayList<Packet> packetList = new ArrayList<>();
    Spinner spinnerMain, spinnerType, spinnerStatus;
    LinearLayout weightLinearLayout, pincodeLinearLayout;
    DatabaseReference reference;
    String userState, userDistrict;
    TextView pincodeRangeSubmit, weightRangeSubmit;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.manage_packages, container, false);

        /*final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference().child("Employees");
        assert currentUser != null;

        /*final String email  = currentUser.getEmail().replace('.', '_');
        final DatabaseReference userInfo = reference2.child(email.replace('.', '_'));
        userInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    userState = snapshot.child("State").getValue(String.class);
                    userDistrict = snapshot.child("District").getValue(String.class);
                    spinnerMain = root.findViewById(R.id.spinnerMain);
                    spinnerStatus = root.findViewById(R.id.spinnerStatus);
                    spinnerType = root.findViewById(R.id.spinnerType);
                    weightLinearLayout = root.findViewById(R.id.weightLinearLayout);
                    pincodeLinearLayout = root.findViewById(R.id.pincodeLinearLayout);

                    final ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.filterMain, layout.simple_spinner_dropdown_item);
                    arrayAdapter.setDropDownViewResource(layout.simple_spinner_dropdown_item);
                    spinnerMain.setAdapter(arrayAdapter);

                    spinnerType.setVisibility(View.GONE);
                    spinnerStatus.setVisibility(View.GONE);
                    weightLinearLayout.setVisibility(View.GONE);
                    pincodeLinearLayout.setVisibility(View.GONE);

                    spinnerMain.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (parent.getItemAtPosition(position).equals("-Select-")) {
//                    displayAll(root, userInfo);

                                spinnerType.setVisibility(View.GONE);
                                spinnerStatus.setVisibility(View.GONE);
                                weightLinearLayout.setVisibility(View.GONE);
                                pincodeLinearLayout.setVisibility(View.GONE);

                                displayAll(root);
                            } else if (parent.getItemAtPosition(position).equals("Type")) {

                                spinnerType.setVisibility(View.VISIBLE);
                                spinnerStatus.setVisibility(View.GONE);
                                weightLinearLayout.setVisibility(View.GONE);
                                pincodeLinearLayout.setVisibility(View.GONE);

                                final ArrayAdapter<CharSequence> arrayAdapterType = ArrayAdapter.createFromResource(requireContext(), R.array.filterType, layout.simple_spinner_dropdown_item);
                                arrayAdapterType.setDropDownViewResource(layout.simple_spinner_dropdown_item);
                                spinnerType.setAdapter(arrayAdapterType);

                                spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        if (parent.getItemAtPosition(position).equals("Letter")) {
                                            filterBySpinner(root, "Type", "Letter");
                                        } else if (parent.getItemAtPosition(position).equals("Envelope")) {
                                            filterBySpinner(root, "Type", "Envelope");
                                        } else if (parent.getItemAtPosition(position).equals("Parcel")) {
                                            filterBySpinner(root, "Type", "Parcel");
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });

                            } else if (parent.getItemAtPosition(position).equals("Pincode")) {
                                pincodeLinearLayout.setVisibility(View.VISIBLE);
                                spinnerStatus.setVisibility(View.GONE);
                                weightLinearLayout.setVisibility(View.GONE);
                                spinnerType.setVisibility(View.GONE);

                                pincodeRangeSubmit = root.findViewById(R.id.pincodeButton);
                                pincodeRangeSubmit.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        EditText From = root.findViewById(R.id.pincodeFromEditText);
                                        EditText To = root.findViewById(R.id.pincodeToEditText);
                                        final int from = Integer.parseInt(From.getText().toString());
                                        final int to = Integer.parseInt(To.getText().toString());

                                        packetList.clear();

                                        reference = FirebaseDatabase.getInstance().getReference();

                                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                Iterable<DataSnapshot> iterable = snapshot.child("Packages").child(userState).child(userDistrict).getChildren();
                                                for (DataSnapshot iter : iterable) {
                                                    String pincodeFromDB = iter.child("Pincode").getValue().toString();
                                                    int pincode = Integer.parseInt(pincodeFromDB);
                                                    if (pincode >= from && pincode <= to) {
                                                        String typeFromDB = iter.child("Type").getValue(String.class);
                                                        String weightFromDB = iter.child("Weight").getValue().toString();
                                                        String statusFromDB = iter.child("Status").getValue(String.class);
                                                        String primaryKeyFromDB = iter.getKey();

                                                        Packet packet = new Packet(typeFromDB, primaryKeyFromDB, pincodeFromDB, weightFromDB, statusFromDB);
                                                        packetList.add(packet);
                                                    }
                                                }

                                                RecyclerViewAdapter displayAllAdapter = new RecyclerViewAdapter(getContext(), packetList);
                                                RecyclerView displayAllRecyclerView = root.findViewById(R.id.recyclerView);
                                                displayAllRecyclerView.setAdapter(displayAllAdapter);

                                                LinearLayoutManager manager = new LinearLayoutManager(getContext());
                                                displayAllRecyclerView.setLayoutManager(manager);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });


                                    }
                                });
                            } else if (parent.getItemAtPosition(position).equals("Status")) {
                                spinnerStatus.setVisibility(View.VISIBLE);
                                spinnerType.setVisibility(View.GONE);
                                weightLinearLayout.setVisibility(View.GONE);
                                pincodeLinearLayout.setVisibility(View.GONE);

                                final ArrayAdapter<CharSequence> arrayAdapterType = ArrayAdapter.createFromResource(requireContext(), R.array.filterStatus, layout.simple_spinner_dropdown_item);
                                arrayAdapterType.setDropDownViewResource(layout.simple_spinner_dropdown_item);
                                spinnerStatus.setAdapter(arrayAdapterType);

                                spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        if (parent.getItemAtPosition(position).equals("Shipped")) {
                                            filterBySpinner(root, "Status", "Shipped");
                                        } else if (parent.getItemAtPosition(position).equals("Arrived")) {
                                            filterBySpinner(root, "Status", "Arrived");
                                        } else if (parent.getItemAtPosition(position).equals("Out for delivery")) {
                                            filterBySpinner(root, "Status", "Out for delivery");
                                        } else if (parent.getItemAtPosition(position).equals("Delivered")) {
                                            filterBySpinner(root, "Status", "Delivered");
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });

                            } else if (parent.getItemAtPosition(position).equals("Weight")) {
                                weightLinearLayout.setVisibility(View.VISIBLE);
                                spinnerStatus.setVisibility(View.GONE);
                                spinnerType.setVisibility(View.GONE);
                                pincodeLinearLayout.setVisibility(View.GONE);

                                weightRangeSubmit = root.findViewById(R.id.weightButton);
                                weightRangeSubmit.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        EditText From = root.findViewById(R.id.weightFromEditText);
                                        EditText To = root.findViewById(R.id.weightToEditText);
                                        final int from = Integer.parseInt(From.getText().toString());
                                        final int to = Integer.parseInt(To.getText().toString());

                                        packetList.clear();

                                        reference = FirebaseDatabase.getInstance().getReference();
                                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                Iterable<DataSnapshot> iterable = snapshot.child("Packages").child(userState).child(userDistrict).getChildren();
                                                for (DataSnapshot iter : iterable) {
                                                    String weightFromDB = iter.child("Weight").getValue().toString();
                                                    int weight = Integer.parseInt(weightFromDB);
                                                    if (weight >= from && weight <= to) {
                                                        String typeFromDB = iter.child("Type").getValue(String.class);
                                                        String pincodeFromDB = iter.child("Pincode").getValue().toString();
                                                        String statusFromDB = iter.child("Status").getValue(String.class);
                                                        String primaryKeyFromDB = iter.getKey();

                                                        Packet packet = new Packet(typeFromDB, primaryKeyFromDB, pincodeFromDB, weightFromDB, statusFromDB);
                                                        packetList.add(packet);
                                                    }
                                                }

                                                RecyclerViewAdapter displayAllAdapter = new RecyclerViewAdapter(getContext(), packetList);
                                                RecyclerView displayAllRecyclerView = root.findViewById(R.id.recyclerView);
                                                displayAllRecyclerView.setAdapter(displayAllAdapter);

                                                LinearLayoutManager manager = new LinearLayoutManager(getContext());
                                                displayAllRecyclerView.setLayoutManager(manager);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                });
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/

        spinnerMain = root.findViewById(R.id.spinnerMain);
        spinnerStatus = root.findViewById(R.id.spinnerStatus);
        spinnerType = root.findViewById(R.id.spinnerType);
        weightLinearLayout = root.findViewById(R.id.weightLinearLayout);
        pincodeLinearLayout = root.findViewById(R.id.pincodeLinearLayout);

        final ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.filterMain, layout.simple_spinner_dropdown_item);
        arrayAdapter.setDropDownViewResource(layout.simple_spinner_dropdown_item);
        spinnerMain.setAdapter(arrayAdapter);

        spinnerType.setVisibility(View.GONE);
        spinnerStatus.setVisibility(View.GONE);
        weightLinearLayout.setVisibility(View.GONE);
        pincodeLinearLayout.setVisibility(View.GONE);

        spinnerMain.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).equals("-Select Filter-")) {
//                    displayAll(root, userInfo);

                    spinnerType.setVisibility(View.GONE);
                    spinnerStatus.setVisibility(View.GONE);
                    weightLinearLayout.setVisibility(View.GONE);
                    pincodeLinearLayout.setVisibility(View.GONE);

                    displayAll(root);
                } else if (parent.getItemAtPosition(position).equals("Type")) {

                    spinnerType.setVisibility(View.VISIBLE);
                    spinnerStatus.setVisibility(View.GONE);
                    weightLinearLayout.setVisibility(View.GONE);
                    pincodeLinearLayout.setVisibility(View.GONE);

                    final ArrayAdapter<CharSequence> arrayAdapterType = ArrayAdapter.createFromResource(requireContext(), R.array.filterType, layout.simple_spinner_dropdown_item);
                    arrayAdapterType.setDropDownViewResource(layout.simple_spinner_dropdown_item);
                    spinnerType.setAdapter(arrayAdapterType);

                    spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (parent.getItemAtPosition(position).equals("Letter")) {
                                filterBySpinner(root, "Type", "Letter");
                            } else if (parent.getItemAtPosition(position).equals("Envelope")) {
                                filterBySpinner(root, "Type", "Envelope");
                            } else if (parent.getItemAtPosition(position).equals("Parcel")) {
                                filterBySpinner(root, "Type", "Parcel");
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                } else if (parent.getItemAtPosition(position).equals("Pincode")) {
                    pincodeLinearLayout.setVisibility(View.VISIBLE);
                    spinnerStatus.setVisibility(View.GONE);
                    weightLinearLayout.setVisibility(View.GONE);
                    spinnerType.setVisibility(View.GONE);

                    pincodeRangeSubmit = root.findViewById(R.id.pincodeButton);
                    pincodeRangeSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EditText From = root.findViewById(R.id.pincodeFromEditText);
                            EditText To = root.findViewById(R.id.pincodeToEditText);
                            final int from = Integer.parseInt(From.getText().toString());
                            final int to = Integer.parseInt(To.getText().toString());

                            packetList.clear();

                            reference = FirebaseDatabase.getInstance().getReference();

                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Iterable<DataSnapshot> iterable = snapshot.child("Packages").child(userState).child(userDistrict).getChildren();
                                    for (DataSnapshot iter : iterable) {
                                        String pincodeFromDB = iter.child("Pincode").getValue().toString();
                                        int pincode = Integer.parseInt(pincodeFromDB);
                                        if (pincode >= from && pincode <= to) {
                                            String typeFromDB = iter.child("Type").getValue(String.class);
                                            String weightFromDB = iter.child("Weight").getValue().toString();
                                            String statusFromDB = iter.child("Status").getValue(String.class);
                                            String primaryKeyFromDB = iter.getKey();

                                            Packet packet = new Packet(typeFromDB, primaryKeyFromDB, pincodeFromDB, weightFromDB, statusFromDB);
                                            packetList.add(packet);
                                        }
                                    }

                                    RecyclerViewAdapter displayAllAdapter = new RecyclerViewAdapter(getContext(), packetList);
                                    RecyclerView displayAllRecyclerView = root.findViewById(R.id.recyclerView);
                                    displayAllRecyclerView.setAdapter(displayAllAdapter);

                                    LinearLayoutManager manager = new LinearLayoutManager(getContext());
                                    displayAllRecyclerView.setLayoutManager(manager);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                        }
                    });
                } else if (parent.getItemAtPosition(position).equals("Status")) {
                    spinnerStatus.setVisibility(View.VISIBLE);
                    spinnerType.setVisibility(View.GONE);
                    weightLinearLayout.setVisibility(View.GONE);
                    pincodeLinearLayout.setVisibility(View.GONE);

                    final ArrayAdapter<CharSequence> arrayAdapterType = ArrayAdapter.createFromResource(requireContext(), R.array.filterStatus, layout.simple_spinner_dropdown_item);
                    arrayAdapterType.setDropDownViewResource(layout.simple_spinner_dropdown_item);
                    spinnerStatus.setAdapter(arrayAdapterType);

                    spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (parent.getItemAtPosition(position).equals("Shipped")) {
                                filterBySpinner(root, "Status", "Shipped");
                            } else if (parent.getItemAtPosition(position).equals("Arrived")) {
                                filterBySpinner(root, "Status", "Arrived");
                            } else if (parent.getItemAtPosition(position).equals("Out for delivery")) {
                                filterBySpinner(root, "Status", "Out for delivery");
                            } else if (parent.getItemAtPosition(position).equals("Delivered")) {
                                filterBySpinner(root, "Status", "Delivered");
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                } else if (parent.getItemAtPosition(position).equals("Weight")) {
                    weightLinearLayout.setVisibility(View.VISIBLE);
                    spinnerStatus.setVisibility(View.GONE);
                    spinnerType.setVisibility(View.GONE);
                    pincodeLinearLayout.setVisibility(View.GONE);

                    weightRangeSubmit = root.findViewById(R.id.weightButton);
                    weightRangeSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EditText From = root.findViewById(R.id.weightFromEditText);
                            EditText To = root.findViewById(R.id.weightToEditText);
                            final int from = Integer.parseInt(From.getText().toString());
                            final int to = Integer.parseInt(To.getText().toString());

                            packetList.clear();

                            reference = FirebaseDatabase.getInstance().getReference();
                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Iterable<DataSnapshot> iterable = snapshot.child("Packages").child(userState).child(userDistrict).getChildren();
                                    for (DataSnapshot iter : iterable) {
                                        String weightFromDB = iter.child("Weight").getValue().toString();
                                        int weight = Integer.parseInt(weightFromDB);
                                        if (weight >= from && weight <= to) {
                                            String typeFromDB = iter.child("Type").getValue(String.class);
                                            String pincodeFromDB = iter.child("Pincode").getValue().toString();
                                            String statusFromDB = iter.child("Status").getValue(String.class);
                                            String primaryKeyFromDB = iter.getKey();

                                            Packet packet = new Packet(typeFromDB, primaryKeyFromDB, pincodeFromDB, weightFromDB, statusFromDB);
                                            packetList.add(packet);
                                        }
                                    }

                                    RecyclerViewAdapter displayAllAdapter = new RecyclerViewAdapter(getContext(), packetList);
                                    RecyclerView displayAllRecyclerView = root.findViewById(R.id.recyclerView);
                                    displayAllRecyclerView.setAdapter(displayAllAdapter);

                                    LinearLayoutManager manager = new LinearLayoutManager(getContext());
                                    displayAllRecyclerView.setLayoutManager(manager);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //userDistrict = "Mumbai";//Need to be commented when merged
        //userState = "Maharashtra";//Need to be commented when merged

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
                userDistrict = snapshot.child("District").getValue(String.class);
                userState = snapshot.child("State").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void filterBySpinner(final View root, final String typeOrStatus, final String value) {
        packetList.clear();

        reference = FirebaseDatabase.getInstance().getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Iterable<DataSnapshot> iterable = snapshot.child("Packages").child(userState).child(userDistrict).getChildren();
                for (DataSnapshot iter : iterable) {
                    if (typeOrStatus.equals("Type")) {
                        String typeFromDB = iter.child("Type").getValue(String.class);
                        if (typeFromDB.equals(value)) {
                            String pincodeFromDB = iter.child("Pincode").getValue().toString();
                            String weightFromDB = iter.child("Weight").getValue().toString();
                            String statusFromDB = iter.child("Status").getValue(String.class);
                            String primaryKeyFromDB = iter.getKey();

                            Packet packet = new Packet(typeFromDB, primaryKeyFromDB, pincodeFromDB, weightFromDB, statusFromDB);
                            packetList.add(packet);
                        }
                    } else {
                        String statusFromDB = iter.child("Status").getValue(String.class);
                        if (statusFromDB.equals(value)) {
                            String pincodeFromDB = iter.child("Pincode").getValue().toString();
                            String weightFromDB = iter.child("Weight").getValue().toString();
                            String typeFromDB = iter.child("Type").getValue(String.class);
                            String primaryKeyFromDB = iter.getKey();

                            Packet packet = new Packet(typeFromDB, primaryKeyFromDB, pincodeFromDB, weightFromDB, statusFromDB);
                            packetList.add(packet);
                        }
                    }

                }

                RecyclerViewAdapter spinnerAdapter = new RecyclerViewAdapter(getContext(), packetList);
                RecyclerView spinnerRecyclerView = root.findViewById(R.id.recyclerView);
                spinnerRecyclerView.setAdapter(spinnerAdapter);

                LinearLayoutManager manager = new LinearLayoutManager(getContext());
                spinnerRecyclerView.setLayoutManager(manager);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void displayAll(final View root) {

        packetList.clear();

        reference = FirebaseDatabase.getInstance().getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Iterable<DataSnapshot> iterable = snapshot.child("Packages").child(userState).child(userDistrict).getChildren();
                for (DataSnapshot iter : iterable) {
                    String typeFromDB = iter.child("Type").getValue(String.class);
                    String pincodeFromDB = iter.child("Pincode").getValue().toString();
                    String weightFromDB = iter.child("Weight").getValue().toString();
                    String statusFromDB = iter.child("Status").getValue(String.class);
                    String primaryKeyFromDB = iter.getKey();

                    Packet packet = new Packet(typeFromDB, primaryKeyFromDB, pincodeFromDB, weightFromDB, statusFromDB);
                    packetList.add(packet);
                }

                RecyclerViewAdapter displayAllAdapter = new RecyclerViewAdapter(getContext(), packetList);
                RecyclerView displayAllRecyclerView = root.findViewById(R.id.recyclerView);
                displayAllRecyclerView.setAdapter(displayAllAdapter);

                LinearLayoutManager manager = new LinearLayoutManager(getContext());
                displayAllRecyclerView.setLayoutManager(manager);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}