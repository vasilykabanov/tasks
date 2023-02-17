package com.example.tasks.tasks;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.icu.util.Calendar;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    public static CustomAdapter adapter;
    private static DataKeeper dataKeeper;

    public PlaceholderFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaceholderFragment newInstance(int sectionNumber) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public void scheduleNotification(Context context, long delay, DataModel data) {
        delay += 10000;
        int notificationId = (int)(Math.random() * 0xFFFFFF);

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent activity = PendingIntent.getActivity(context, notificationId, intent, PendingIntent.FLAG_CANCEL_CURRENT);


        Notification.Builder builder = new Notification.Builder(context)
                .setContentTitle(data.title)
                .setContentText(data.description)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(new Notification.BigTextStyle().bigText(data.description))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .addAction(R.mipmap.ic_launcher, "ButtonU", activity)
                .setContentIntent(activity);
/*
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "tasks_noti")
                .setContentTitle(data.title)
                //.setContentText(data.description)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .addAction(R.mipmap.ic_launcher, "ButtonU", activity)
                .setContentIntent(activity);
*/
 //       builder.setStyle( new Notification.MediaStyle().setShowActionsInCompactView(1));

        Notification notification = builder.build();

        Intent notificationIntent = new Intent(context, MyNotificationPublisher.class);
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION_ID, notificationId);
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView;
        switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
            case 1:
                rootView = inflater.inflate(R.layout.fragment_main, container, false);
                final RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
                recyclerView.setHasFixedSize(true);

                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(rootView.getContext());
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());

                if (dataKeeper == null) {
                    dataKeeper = new DataKeeper(getContext());
                }

                if (adapter == null) {
                    adapter = new CustomAdapter(dataKeeper);
                }
                recyclerView.setAdapter(adapter);

                View view = getActivity().findViewById(android.R.id.content);
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().getBaseContext().INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }


                ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                        Log.d("TAsKS", Integer.toString(viewHolder.getAdapterPosition()));
                        if (viewHolder.getAdapterPosition() == 0) {
                            return;
                        }
                        if (swipeDir == ItemTouchHelper.LEFT) {
                            dataKeeper.remove(viewHolder.getAdapterPosition());
                            adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                        }
                        if (swipeDir == ItemTouchHelper.RIGHT) {
                            dataKeeper.toggleDone(viewHolder.getAdapterPosition());
                            adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                        }
                    }

                    @Override
                    public void onMoved(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int fromPos, RecyclerView.ViewHolder target, int toPos, int x, int y) {
                        super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
                    }

                    @Override
                    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                        if (viewHolder.getAdapterPosition() == 0) {
                            return;
                        }
                        View itemView = viewHolder.itemView;

                        Drawable d = ContextCompat.getDrawable(getActivity(), R.color.colorDelete);
                        d.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                        d.draw(c);

                        d = ContextCompat.getDrawable(getActivity(), R.color.colorDone);
                        d.setBounds(itemView.getLeft(), itemView.getTop(), (int) dX, itemView.getBottom());
                        d.draw(c);

                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    }
                };

                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
                itemTouchHelper.attachToRecyclerView(recyclerView);

                break;
            case 2:
                rootView = inflater.inflate(R.layout.fragment_add, container, false);

                final EditText edittext= (EditText) rootView.findViewById(R.id.editDate);
                SimpleDateFormat simpleDate = new SimpleDateFormat("dd/MM/yyyy");
                Date dt = new Date();
                try {
                    dt = simpleDate.parse(edittext.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                final Date fdt = dt;
                final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        fdt.setYear(year - 1900);
                        fdt.setMonth(monthOfYear);
                        fdt.setDate(dayOfMonth);
                        updateLabel();
                    }

                    private void updateLabel() {
                        String myFormat = "dd/MM/yyy"; //In which you need put here
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);

                        fdt.setYear(fdt.getYear());
                        edittext.setText(sdf.format(fdt));
                    }

                };

                edittext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DatePickerDialog(rootView.getContext(),
                                date,
                                fdt.getYear() + 1900,//myCalendar.get(Calendar.YEAR),
                                fdt.getMonth(),//myCalendar.get(Calendar.MONTH),
                                fdt.getDate()//myCalendar.get(Calendar.DAY_OF_MONTH)
                        ).show();
                    }
                });

                Button clickButton = (Button) rootView.findViewById(R.id.buttonAddEdit);
                clickButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView title = (TextView) rootView.findViewById(R.id.editTitle);
                        TextView date = (TextView) rootView.findViewById(R.id.editDate);
                        TextView description = (TextView) rootView.findViewById(R.id.editDescription);
                        RadioButton highPriority = (RadioButton) rootView.findViewById(R.id.radioPriorityHigh);
                        RadioButton middlePriority = (RadioButton) rootView.findViewById(R.id.radioPriorityMiddle);
                        RadioButton lowPriority = (RadioButton) rootView.findViewById(R.id.radioPriorityLow);
                        Switch notify = (Switch) rootView.findViewById(R.id.switchNotify);

                        Integer priority = 0;
                        if (highPriority.isChecked()) {
                            priority = 2;
                        }
                        if (middlePriority.isChecked()) {
                            priority = 1;
                        }
                        if (DataKeeper.editId != -1) {
                            dataKeeper.editItem(DataKeeper.editId, title.getText().toString(), date.getText().toString(), description.getText().toString(), priority, notify.isChecked(), false);
                            adapter.notifyDataSetChanged();
                            DataKeeper.editId = -1;
                        } else {
                            adapter.addElement(title.getText().toString(), date.getText().toString(), description.getText().toString(), priority, notify.isChecked(), false);
                        }

                        Toast.makeText(getContext(), "Saved", Toast.LENGTH_LONG).show();

                        SimpleDateFormat simpleDate = new SimpleDateFormat("dd/MM/yyyy");
                        Date dt = new Date();
                        try {
                            dt = simpleDate.parse(date.getText().toString());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if (dt.getTime() - new Date().getTime() < 1000) {
                            scheduleNotification(rootView.getContext(), 5000, dataKeeper.get(dataKeeper.getData().size() - 1));
                        }
                        scheduleNotification(rootView.getContext(), dt.getTime() - new Date().getTime() + 8*60*60*1000, dataKeeper.get(dataKeeper.getData().size() - 1));
                        scheduleNotification(rootView.getContext(), dt.getTime() - new Date().getTime() + 12*60*60*1000, dataKeeper.get(dataKeeper.getData().size() - 1));
                        scheduleNotification(rootView.getContext(), dt.getTime() - new Date().getTime() + 15*60*60*1000, dataKeeper.get(dataKeeper.getData().size() - 1));

                        title.setText("");
                        date.setText("");
                        description.setText("");
                        highPriority.setChecked(false);
                        middlePriority.setChecked(false);
                        lowPriority.setChecked(true);
                        notify.setChecked(false);

                        View t = (View) rootView.getParent().getParent().getParent().getParent().getParent();
                        DataKeeper.state = DataKeeper.ST_OPEN;
                        PlaceholderFragment.adapter.update();
                        ((Toolbar) t.findViewById(R.id.toolbar)).setTitle("Задачи");
                        TabLayout p = t.findViewById(R.id.tabs);
                        TabLayout.Tab tab = p.getTabAt(0);
                        tab.select();

                        View view = getActivity().findViewById(android.R.id.content);
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().getBaseContext().INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                    }
                });
                break;
            case 3:
                rootView = inflater.inflate(R.layout.fragment_settings, container, false);
                Button clearAllButton = (Button) rootView.findViewById(R.id.buttonClearAll);
                clearAllButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dataKeeper.clearAll();
                        adapter.notifyDataSetChanged();
                        Toast.makeText(rootView.getContext(), "Clear all data", Toast.LENGTH_LONG).show();
                    }
                });

                Switch pinSwitch = (Switch) rootView.findViewById(R.id.switchPin);
                SharedPreferences settings = getContext().getSharedPreferences("TaskPrefs", Activity.MODE_PRIVATE);
                Boolean hasPin = settings.getBoolean("hasPin", false);
                pinSwitch.setChecked(hasPin);

                EditText pinEdit = (EditText) rootView.findViewById(R.id.editPin);
                pinEdit.setVisibility(hasPin ? View.VISIBLE : View.GONE);
                pinSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        SharedPreferences sharedPref = getActivity().getSharedPreferences("TaskPrefs", getContext().MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean("hasPin", isChecked);
                        EditText pinEdit = (EditText) rootView.findViewById(R.id.editPin);
                        pinEdit.setText("");
                        editor.putString("pin", "");
                        editor.commit();
                        if (isChecked) {
                            pinEdit.setVisibility(View.VISIBLE);
                        } else {
                            pinEdit.setVisibility(View.GONE);
                        }
                    }
                });

                EditText pinText = (EditText) rootView.findViewById(R.id.editPin);
                pinText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        SharedPreferences sharedPref = getActivity().getSharedPreferences("TaskPrefs", getContext().MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("pin", s.toString());
                        editor.commit();
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });


                break;
            default:
                rootView = inflater.inflate(R.layout.fragment_main, container, false);
                break;
        }
        return rootView;
    }
}