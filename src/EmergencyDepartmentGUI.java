import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class EmergencyDepartmentGUI extends JFrame {
    private EmergencyDepartment ed;
    private final Color background = new Color(239,244,250), ink = new Color(24,43,68);
    private static final Color[] COLORS = {new Color(255,210,212),new Color(255,225,185),new Color(255,244,172),new Color(199,226,255),new Color(201,240,216)};
    private static final String[] LEVELS = {"1 · Critical", "2 · Emergent", "3 · Urgent", "4 · Less urgent", "5 · Non-urgent"};
    private final JLabel stats = new JLabel(), feedback = new JLabel("Ready. Register a patient or load the demo.");
    private final DefaultTableModel queueModel = model("ID","Patient","Condition","Triage","Arrival","Waiting");
    private final DefaultTableModel historyModel = model("ID","Patient","Triage","Arrival","Admitted","Discharged","Wait","Treatment");
    private final JTable queue = new JTable(queueModel), history = new JTable(historyModel);
    private final JButton[] roomButtons = new JButton[10];
    private final JTextField id = new JTextField(), name = new JTextField(), condition = new JTextField();
    private final JComboBox<String> triage = new JComboBox<>(LEVELS);
    private final javax.swing.Timer timer;
    public EmergencyDepartmentGUI(EmergencyDepartment department) {
        super("Emergency Department Simulator · Java 21");
        ed = department; setDefaultCloseOperation(EXIT_ON_CLOSE); setSize(1250,820); setMinimumSize(new Dimension(1000,700)); setLocationRelativeTo(null);
        JPanel root = new JPanel(new BorderLayout(18,18)); root.setBackground(background); root.setBorder(BorderFactory.createEmptyBorder(22,22,18,22)); setContentPane(root);
        JPanel header = new JPanel(new GridLayout(3,1,0,7)); header.setOpaque(false);
        JLabel title = new JLabel("EMERGENCY DEPARTMENT"); title.setFont(new Font("SansSerif",Font.BOLD,27)); title.setForeground(ink);
        header.add(title); header.add(new JLabel("Patient flow simulator  /  severity first · FIFO within each level")); stats.setFont(new Font("SansSerif",Font.BOLD,16)); header.add(stats); root.add(header,BorderLayout.NORTH);
        JPanel center = new JPanel(new BorderLayout(16,16)); center.setOpaque(false); root.add(center);
        JPanel registration = new JPanel(new GridLayout(0,1,4,7)); registration.setPreferredSize(new Dimension(220,400)); registration.setBorder(BorderFactory.createTitledBorder("Register patient"));
        registration.add(new JLabel("Patient ID (unique)")); registration.add(id); registration.add(new JLabel("Full name")); registration.add(name);
        registration.add(new JLabel("Condition")); registration.add(condition); registration.add(new JLabel("Triage level")); registration.add(triage);
        registration.add(button("Register patient", () -> { ed.registerPatient(new Patient(id.getText(),name.getText(),condition.getText(),triage.getSelectedIndex()+1)); id.setText(""); name.setText(""); condition.setText(""); return "Patient registered."; }));
        registration.add(button("Search by ID", () -> { String value = JOptionPane.showInputDialog(this,"Patient ID"); if(value == null) return "Search cancelled."; Patient p = ed.searchPatient(value); if(p == null) return "No patient found."; showPatient(p); return "Patient found."; }));
        registration.add(button("Load demo", () -> { if(ed.totalPatients() > 0 && JOptionPane.showConfirmDialog(this,"Replace current simulator data with fictional demo patients?","Load demo",JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return "Demo cancelled."; ed = EmergencyDepartment.demo(); return "Fictional demo loaded."; }));
        center.add(registration,BorderLayout.WEST);
        JPanel flow = new JPanel(new BorderLayout(12,12)); flow.setOpaque(false); center.add(flow);
        styleTable(queue,true); styleTable(history,false);
        JTabbedPane tabs = new JTabbedPane(); tabs.addTab("Waiting queue · priority order",new JScrollPane(queue)); tabs.addTab("Discharge history · newest first",new JScrollPane(history));
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT)); actions.setOpaque(false);
        actions.add(button("Peek next", () -> { Patient p = ed.peekNextPatient(); if(p == null) return "Waiting queue is empty."; showPatient(p); return "Next: " + p; }));
        actions.add(button("Call / get next → admit", () -> { Patient p = ed.getNextPatient(); return p == null ? "Waiting queue is empty." : "Admitted: " + p; }));
        actions.add(button("Admit selected", () -> { Patient p = selected(); String value = JOptionPane.showInputDialog(this,"Room number (1–10)"); if(value == null) return "Admission cancelled."; ed.admitPatient(Integer.parseInt(value.trim()),p); return "Admitted to room " + value; }));
        JPanel waiting = new JPanel(new BorderLayout()); waiting.add(actions,BorderLayout.NORTH); waiting.add(tabs); flow.add(waiting);
        JPanel rooms = new JPanel(new GridLayout(2,5,8,8)); rooms.setPreferredSize(new Dimension(800,195)); rooms.setBorder(BorderFactory.createTitledBorder("10 treatment rooms · click an occupied room to discharge"));
        for(int i=0;i<10;i++) { final int number=i+1; roomButtons[i]=button("",()-> { Room room=ed.rooms().get(number-1); if(!room.isOccupied()) return "Room "+number+" is available."; if(JOptionPane.showConfirmDialog(this,"Discharge "+room.getPatient().getName()+"?","Discharge",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION) return "Discharge cancelled."; Patient p=ed.dischargePatient(number); return "Discharged: "+p; }); rooms.add(roomButtons[i]); }
        flow.add(rooms,BorderLayout.SOUTH);
        JPanel footer = new JPanel(new BorderLayout()); footer.setOpaque(false); feedback.setForeground(ink); footer.add(feedback);
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.RIGHT,5,0)); legend.setOpaque(false);
        for(int i=0;i<5;i++) { JLabel label=new JLabel(" "+LEVELS[i]+" "); label.setOpaque(true); label.setBackground(COLORS[i]); legend.add(label); } footer.add(legend,BorderLayout.SOUTH); root.add(footer,BorderLayout.SOUTH);
        timer = new javax.swing.Timer(1000,e->refresh()); timer.start(); refresh();
    }
    @Override public void dispose() { timer.stop(); super.dispose(); }
    private interface Action { String run(); }
    private JButton button(String label, Action action) {
        JButton b=new JButton(label); b.setFont(new Font("SansSerif",Font.BOLD,12)); b.addActionListener(e->{
            try { feedback.setText(action.run()); }
            catch(RuntimeException ex) { feedback.setText(ex.getMessage()); JOptionPane.showMessageDialog(this,ex.getMessage(),"Action could not be completed",JOptionPane.WARNING_MESSAGE); }
            refresh();
        }); return b;
    }
    private Patient selected() {
        int row=queue.getSelectedRow(); if(row<0) throw new IllegalArgumentException("Select a waiting patient first.");
        return ed.searchPatient(queueModel.getValueAt(queue.convertRowIndexToModel(row),0).toString());
    }
    private static DefaultTableModel model(String... columns) { return new DefaultTableModel(columns,0) { @Override public boolean isCellEditable(int r,int c) { return false; } }; }
    private void styleTable(JTable table, boolean waiting) {
        table.setRowHeight(34); table.setFont(new Font("SansSerif",Font.PLAIN,13)); table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); table.setFillsViewportHeight(true);
        table.setDefaultRenderer(Object.class,new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t,Object v,boolean selected,boolean focus,int r,int c) {
                Component comp=super.getTableCellRendererComponent(t,v,selected,focus,r,c);
                Patient p=ed.searchPatient(t.getValueAt(r,0).toString());
                if(!selected) { comp.setBackground(p==null?Color.WHITE:COLORS[p.getSeverity()-1]); comp.setForeground(ink); }
                return comp;
            }
        });
    }
    private static String time(LocalDateTime t) { return t==null?"—":t.format(DateTimeFormatter.ofPattern("MMM d HH:mm:ss")); }
    private static String duration(Duration d) { long s=Math.max(0,d.getSeconds()); return String.format("%d:%02d:%02d",s/3600,s/60%60,s%60); }
    private void showPatient(Patient p) {
        JOptionPane.showMessageDialog(this,p+"\nCondition: "+p.getCondition()+"\nStatus: "+p.status()+"\nArrival: "+time(p.getArrivalTime())+"\nAdmission: "+time(p.getAdmissionTime())+"\nDischarge: "+time(p.getDischargeTime())+"\nWait: "+duration(p.waitingDuration(ed.now()))+"\nTreatment: "+duration(p.treatmentDuration(ed.now())),"Patient details",JOptionPane.INFORMATION_MESSAGE);
    }
    private void refresh() {
        String selectedId=queue.getSelectedRow()<0?null:queue.getValueAt(queue.getSelectedRow(),0).toString();
        LocalDateTime now=ed.now(); queueModel.setRowCount(0);
        for(Patient p:ed.waitingPatients()) { queueModel.addRow(new Object[]{p.getId(),p.getName(),p.getCondition(),LEVELS[p.getSeverity()-1],time(p.getArrivalTime()),duration(p.waitingDuration(now))}); if(p.getId().equals(selectedId)) queue.setRowSelectionInterval(queueModel.getRowCount()-1,queueModel.getRowCount()-1); }
        historyModel.setRowCount(0);
        for(Patient p:ed.dischargeHistory()) historyModel.addRow(new Object[]{p.getId(),p.getName(),LEVELS[p.getSeverity()-1],time(p.getArrivalTime()),time(p.getAdmissionTime()),time(p.getDischargeTime()),duration(p.waitingDuration(now)),duration(p.treatmentDuration(now))});
        List<Room> rooms=ed.rooms();
        for(int i=0;i<10;i++) { Patient p=rooms.get(i).getPatient(); roomButtons[i].setBackground(p==null?Color.WHITE:COLORS[p.getSeverity()-1]); roomButtons[i].setText(p==null?"<html>ROOM "+(i+1)+"<br>Available</html>":"<html>ROOM "+(i+1)+"<br>"+escape(p.getName())+"<br>"+LEVELS[p.getSeverity()-1]+"<br>Treatment "+duration(p.treatmentDuration(now))+"</html>"); }
        stats.setText("REGISTERED  "+ed.totalPatients()+"     WAITING  "+ed.waitingCount()+"     OCCUPIED  "+ed.occupiedCount()+" / 10     AVAILABLE  "+ed.availableCount()+"     DISCHARGED  "+ed.dischargeHistory().size());
    }
    private static String escape(String s) { return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;"); }
}
