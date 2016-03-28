package com.nshmura.strictmodenotifier.internal;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.nshmura.strictmodenotifier.R;
import java.util.ArrayList;
import java.util.List;

class ReportAdapter extends BaseAdapter {
  List<StrictModeReport> reports = new ArrayList<>();
  private StrictModeReportActivity reportActivity;

  public ReportAdapter(StrictModeReportActivity reportActivity) {
    this.reportActivity = reportActivity;
  }

  @Override public int getCount() {
    return reports.size();
  }

  @Override public StrictModeReport getItem(int position) {
    return reports.get(position);
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public View getView(int position, View convertView, ViewGroup parent) {
    StrictModeReport report = getItem(position);

    if (convertView == null) {
      convertView = LayoutInflater.from(reportActivity)
          .inflate(R.layout.strictmode_notifier_row, parent, false);
      convertView.setTag(new ViewHolder(convertView));
    }

    ViewHolder holder = (ViewHolder) convertView.getTag();
    holder.numberText.setText(
        parent.getContext().getString(R.string.strictmode_count, getCount() - position));
    holder.dateText.setText(report.getDateText(reportActivity));

    if (report.violationType != null) {
      holder.violationTypeText.setText(report.violationType.violationName());
    } else {
      holder.violationTypeText.setText(report.note);
    }

    return convertView;
  }

  public void addAll(List<StrictModeReport> reports) {
    this.reports.addAll(reports);
  }

  public void clear() {
    this.reports.clear();
  }

  private class ViewHolder {
    final TextView numberText;
    final TextView violationTypeText;
    final TextView dateText;

    public ViewHolder(View convertView) {
      numberText = (TextView) convertView.findViewById(R.id.__number);
      violationTypeText = (TextView) convertView.findViewById(R.id.__violation_type);
      dateText = (TextView) convertView.findViewById(R.id.__date);
    }
  }
}