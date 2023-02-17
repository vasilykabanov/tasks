package com.example.tasks.tasks;

import android.support.design.widget.TabLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private DataKeeper dataKeeper;

    public void addElement(String title, String date, String description, Integer priority, boolean notify, boolean done) {
        SimpleDateFormat simpleDate = new SimpleDateFormat("dd/MM/yyyy");
        Date dt = new Date();
        try {
            dt = simpleDate.parse(date);
            System.out.println(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        dataKeeper.add(new DataModel(
                title,
                description,
                dt,
                priority,
                notify,
                done
        ));
        notifyDataSetChanged();
    }

    public void update() {
        dataKeeper.update();
        notifyDataSetChanged();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewDate;
        private TextView textViewTitle;
        private TextView textViewDescription;
        private TextView textViewPriority;
        private TextView textMenuBtn;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.textViewTitle = (TextView) itemView.findViewById(R.id.textViewTitle);
            this.textViewDate = (TextView) itemView.findViewById(R.id.textViewDate);
            this.textViewDescription = (TextView) itemView.findViewById(R.id.textViewDescription);
            this.textViewPriority = (TextView) itemView.findViewById(R.id.textPriority);
            this.textMenuBtn = (TextView) itemView.findViewById(R.id.textMenuBtn);
        }
    }

    public static class MyViewHolder2 extends MyViewHolder {

        public MyViewHolder2(View itemView) {
            super(itemView);
        }
    }

    public CustomAdapter(DataKeeper data) {
        dataKeeper = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        if (viewType != 1) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.calendarview, parent, false);

            CalendarView calendarView = (CalendarView) view.findViewById(R.id.calendarView);
            calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

                @Override
                public void onSelectedDayChange(CalendarView view, int year, int month,
                                                int dayOfMonth) {
                    DataKeeper.selectedDate = new Date(year - 1900, month, dayOfMonth);

                    View t = (View) view.getParent().getParent().getParent().getParent().getParent();

                    TextView date = (TextView) t.findViewById(R.id.editDate);
                    SimpleDateFormat simpleDate = new SimpleDateFormat("dd/MM/yyyy");
                    date.setText(simpleDate.format(DataKeeper.selectedDate));

                    TabLayout p = t.findViewById(R.id.tabs);
                    TabLayout.Tab tab = p.getTabAt(1);
                    tab.select();
                }
            });

            //view.setOnClickListener(MainActivity.myOnClickListener);

            MyViewHolder2 myViewHolder = new MyViewHolder2(view);
            return myViewHolder;
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.cardview, parent, false);

            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position != 0) {
            return 1;
        }
        return 0;
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
        if (holder instanceof MyViewHolder2) {
            return;
        }
        TextView textViewName = holder.textViewTitle;
        TextView textViewDate = holder.textViewDate;
        TextView textViewVersion = holder.textViewDescription;
        TextView textViewPriority = holder.textViewPriority;

        DataModel element = dataKeeper.get(listPosition);
        textViewName.setText(element.title);

        SimpleDateFormat simpleDate = new SimpleDateFormat("dd/MM/yyyy");
        textViewDate.setText(simpleDate.format(element.date));
        textViewVersion.setText(element.description);

        switch (element.priority) {
            case 1:
                textViewPriority.setBackgroundResource(android.R.color.holo_orange_light);
                break;
            case 2:
                textViewPriority.setBackgroundResource(android.R.color.holo_red_light);
                break;
            case 0:
            default:
                textViewPriority.setBackgroundResource(android.R.color.holo_green_light);
        }

        holder.textMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(holder.textMenuBtn.getContext(), holder.textMenuBtn);
                popupMenu.inflate(R.menu.option_menu);

                MenuItem mi = popupMenu.getMenu().findItem(R.id.mnu_item_done);
                if (DataKeeper.state == DataKeeper.ST_OPEN) {
                    mi.setTitle("Сделано");
                } else {
                    mi.setTitle("Не сделано");
                }

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.mnu_item_edit:
                                dataKeeper.editId = listPosition;
                                View t = (View) holder.textMenuBtn.getParent().getParent().getParent().getParent().getParent().getParent().getParent().getParent().getParent();
                                TabLayout p = t.findViewById(R.id.tabs);
                                TabLayout.Tab tab = p.getTabAt(1);
                                tab.select();

                                setEditData(t);

                                break;
                            case R.id.mnu_item_done:
                                dataKeeper.toggleDone(listPosition);
                                notifyDataSetChanged();
                                if (DataKeeper.state == DataKeeper.ST_OPEN) {
                                    Toast.makeText(holder.textMenuBtn.getContext(), "Перемещено в сделанное", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(holder.textMenuBtn.getContext(), "Перемещено в открытые", Toast.LENGTH_LONG).show();
                                }
                                break;
                            case R.id.mnu_item_delete:
                                dataKeeper.remove(listPosition);
                                notifyDataSetChanged();
                                Toast.makeText(holder.textMenuBtn.getContext(), "Удалено", Toast.LENGTH_LONG).show();
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

    }

    private void setEditData(View rootView) {
        if (dataKeeper.editId != -1) {
            DataModel item = dataKeeper.get(dataKeeper.editId);
            if (item != null) {
                TextView title = (TextView) rootView.findViewById(R.id.editTitle);
                TextView date = (TextView) rootView.findViewById(R.id.editDate);
                TextView description = (TextView) rootView.findViewById(R.id.editDescription);
                RadioButton highPriority = (RadioButton) rootView.findViewById(R.id.radioPriorityHigh);
                RadioButton middlePriority = (RadioButton) rootView.findViewById(R.id.radioPriorityMiddle);
                RadioButton lowPriority = (RadioButton) rootView.findViewById(R.id.radioPriorityLow);
                Switch notify = (Switch) rootView.findViewById(R.id.switchNotify);
                Button buttonAddEdit = (Button) rootView.findViewById(R.id.buttonAddEdit);

                title.setText(item.title);

                SimpleDateFormat simpleDate = new SimpleDateFormat("dd/MM/yyyy");
                date.setText(simpleDate.format(item.date));
                description.setText(item.description);

                switch (item.priority) {
                    case 1:
                        middlePriority.setChecked(true);
                        break;
                    case 2:
                        highPriority.setChecked(true);
                        break;
                    case 0:
                    default:
                        lowPriority.setChecked(true);
                }

                notify.setChecked(item.notification);

                buttonAddEdit.setText("Изменить");
            }

        }
    }

    @Override
    public int getItemCount() {
        return dataKeeper.size();
    }
}
