# MyCityPicker
日期选择控件



                TimePicker timePicker = new TimePicker.Builder(MainActivity.this)
                        .monthCyclic(false)
                        .yearCyclic(false)
                        .defaultMonth("3")
                        .defaultDay("10")
                        .build();

                timePicker.show();

                timePicker.setOnDayItemClickListener(new TimePicker.OnDayItemClickListener() {
                    @Override
                    public void onSelected(String... daySelected) {
                        tv.setText(daySelected[0]+"年\n" +daySelected[1] + "月\n"+ daySelected[2]+ "日");

                    }
                });
