package jdbc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import java.sql.*;
import java.util.*;

public class company extends JFrame implements ActionListener {
	
	private Connection conn;
	
	private JLabel dbnameLabel = new JLabel("DB명: ");
	private JLabel userLabel = new JLabel("user: ");
	private JLabel pwdLabel = new JLabel("password: ");
	private JLabel searchLabel = new JLabel("검색 항목: ");
	private JLabel empCountLabel = new JLabel("인원 수: ");
	private JLabel empCount = new JLabel("0");
	
	private JTextField dbnameText = new JTextField(10);
	private JTextField userText = new JTextField(10);
	private JTextField pwdText = new JTextField(10);
	
	private JCheckBox nameCB = new JCheckBox("Name", true);
	private JCheckBox ssnCB = new JCheckBox("Ssn", true);
	private JCheckBox bDateCB = new JCheckBox("Bdate", true);
	private JCheckBox addressCB = new JCheckBox("Address", true);
	private JCheckBox sexCB = new JCheckBox("Sex", true);
	private JCheckBox salaryCB = new JCheckBox("Salary", true);
	private JCheckBox supervisorCB = new JCheckBox("Supervisor", true);
	private JCheckBox departmentCB = new JCheckBox("Department", true);
	
	private JButton connBtn = new JButton("연결");
	private JButton searchBtn = new JButton("검색");
	private JButton addEmployeeBtn = new JButton("직원 추가");

	private JTable resultTable;
	private DefaultTableModel resultModel;
	private JScrollPane scrollPane;

	private ArrayList<String> columnNames = new ArrayList<String>();
	
	public company() {

		// DB 연결 관련 코드
		JPanel dbConnPanel = new JPanel();
		dbConnPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
		dbConnPanel.add(dbnameLabel);
		dbConnPanel.add(dbnameText);
		dbConnPanel.add(userLabel);
		dbConnPanel.add(userText);
		dbConnPanel.add(pwdLabel);
		dbConnPanel.add(pwdText);
		dbConnPanel.add(connBtn);
		
		// 검색 관련 코드
		JPanel searchPanel = new JPanel();
		searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
		searchPanel.add(searchLabel);
		searchPanel.add(nameCB);
		searchPanel.add(ssnCB);
		searchPanel.add(bDateCB);
		searchPanel.add(addressCB);
		searchPanel.add(sexCB);
		searchPanel.add(salaryCB);
		searchPanel.add(supervisorCB);
		searchPanel.add(departmentCB);
		searchPanel.add(searchBtn);
		searchPanel.add(addEmployeeBtn);

		// 결과창 관련 코드
		JPanel resultPanel = new JPanel();
		resultPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        resultModel = new DefaultTableModel();
        resultTable = new JTable(resultModel);
        scrollPane = new JScrollPane(resultTable);
        scrollPane.setPreferredSize(new Dimension(1000, 400));
		
		resultPanel.add(scrollPane);
		
		
		// 하나의 큰 Top panel에 DB 연결, 검색, 결과창 panel을 집어넣음
		JPanel Top = new JPanel();
		Top.setLayout(new BoxLayout(Top, BoxLayout.Y_AXIS));
		Top.add(dbConnPanel);
		Top.add(searchPanel);
		Top.add(resultPanel);
		
		add(Top, BorderLayout.NORTH);
		
		
		// 버튼과 이벤트 처리 함수 연결
		connBtn.addActionListener(this);
		searchBtn.addActionListener(this);
		addEmployeeBtn.addActionListener(e -> openAddEmployeeDialog());

		setSize(1200, 800); // 창 크기 세팅
		setLocationRelativeTo(null); // 창이 가운데 위치하도록
		setTitle("103조 JDBC 프로젝트"); // 타이틀 세팅
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 창을 닫았을 때 프로그램이 종료되도록 설정
		setVisible(true); // 창이 보이도록
	}

	// 이벤트 발생 처리 함수
	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == connBtn) { // DB 연결 버튼이 클릭되었을 때

			try {
				String dbname = dbnameText.getText();
				String user = userText.getText();
				String pwd = pwdText.getText();
				
				String url = "jdbc:mysql://localhost:3306/" + dbname + "?serverTimeZone=UTC";
				conn = DriverManager.getConnection(url, user, pwd);
				System.out.println("DB에 연결되었습니다.");
				connBtn.setForeground(Color.blue);
				connBtn.setText("연결 완료!!");
						
			} catch (SQLException e1) {
				System.err.println("DB 연결에 실패했습니다.");
				connBtn.setForeground(Color.red);
				connBtn.setText("연결 실패");
				e1.printStackTrace();
			}
		}

		if (e.getSource() == searchBtn) { // 검색 버튼이 클릭되었을 때
		
			
			String query = getQuery(); // select 하는 쿼리 가져오기
			
			if (query.length() > 0) {
				try {
					Statement stmt = conn.createStatement();
					ResultSet rs = stmt.executeQuery(query);
					int colCount = rs.getMetaData().getColumnCount(); // 열의 갯수 체크

					resultModel = new DefaultTableModel(columnNames.toArray(), 0);
					while (rs.next()) {
						ArrayList<Object> row = new ArrayList<>();
						for (int i = 1; i <= colCount; i++) {
							row.add(rs.getObject(i));
						}
						
						resultModel.addRow(row.toArray());
					}
					
					resultTable = new JTable(resultModel);
					scrollPane.setViewportView(resultTable);
					revalidate();
					repaint();
				}
				catch (SQLException e1) {
					e1.printStackTrace();
					System.err.println("DB 검색 중 실패했습니다.");
				}
			}
			else {
				System.err.println("선택된 attribute가 없습니다.");
			}
		}
	}

	// 검색 쿼리 가져오는 함수
	private String getQuery() {
		int selectedCount = 0;
		String query = "select ";
		columnNames.clear();
		
		if (nameCB.isSelected()) {
			query += "CONCAT(Fname, ' ', Minit, ' ', Lname)";
			selectedCount += 1;
			columnNames.add("NAME");
		}

		if (ssnCB.isSelected()) {
			if (selectedCount != 0) {
				query += ",";
			}
			query += "ssn";
			selectedCount += 1;
			columnNames.add("SSN");
		}

		if (bDateCB.isSelected()) {
			if (selectedCount != 0) {
				query += ",";
			}
			query += "Bdate";
			selectedCount += 1;
			columnNames.add("BDATE");
		}

		if (addressCB.isSelected()) {
			if (selectedCount != 0) {
				query += ",";
			}
			query += "address";
			selectedCount += 1;
			columnNames.add("ADDRESS");
		}

		if (sexCB.isSelected()) {
			if (selectedCount != 0) {
				query += ",";
			}
			query += "sex";
			selectedCount += 1;
			columnNames.add("SEX");
		}

		if (salaryCB.isSelected()) {
			if (selectedCount != 0) {
				query += ",";
			}
			query += "salary";
			selectedCount += 1;
			columnNames.add("SALARY");
		}

		if (supervisorCB.isSelected()) {
			if (selectedCount != 0) {
				query += ",";
			}
			query += "(select CONCAT(Fname, ' ', Minit, ' ', Lname) from company.Employee where ssn = E.Super_ssn)";
			selectedCount += 1;
			columnNames.add("SUPERVISOR");
		}

		if (departmentCB.isSelected()) {
			if (selectedCount != 0) {
				query += ",";
			}
			query += "(select Dname from company.department where Dnumber = E.Dno)";
			selectedCount += 1;
			columnNames.add("DEPARTMENT");
		}
		
		query += " from company.employee E";
		
		if (selectedCount == 0) {
			query = "";
		}
		
		return query;
	}

	private void openAddEmployeeDialog() {
		JDialog addEmployeeDialog = new JDialog(this, "새로운 직원 정보 추가", true);
		String[] genders = {"F", "M"};
		JComboBox<String> sexComboBox = new JComboBox<>(genders);
		addEmployeeDialog.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 5, 5, 5);

		JTextField firstNameField = new JTextField(10);
		JTextField middleInitField = new JTextField(1);
		JTextField lastNameField = new JTextField(10);
		JTextField ssnField = new JTextField(9);
		JTextField birthdateField = new JTextField(10);
		JTextField addressField = new JTextField(20);
		JComboBox<String> sexField = sexComboBox;
		JTextField salaryField = new JTextField(10);
		JTextField superSsnField = new JTextField(9);
		JTextField dnoField = new JTextField(2);
		JButton addBtn = new JButton("정보 추가하기");

		gbc.gridx = 0;
		gbc.gridy = 0;
		addEmployeeDialog.add(new JLabel("First Name:"), gbc);
		gbc.gridx = 1;
		addEmployeeDialog.add(firstNameField, gbc);

		// Middle Init
		gbc.gridx = 0;
		gbc.gridy = 1;
		addEmployeeDialog.add(new JLabel("Middle Init:"), gbc);
		gbc.gridx = 1;
		addEmployeeDialog.add(middleInitField, gbc);

		// Last Name
		gbc.gridx = 0;
		gbc.gridy = 2;
		addEmployeeDialog.add(new JLabel("Last Name:"), gbc);
		gbc.gridx = 1;
		addEmployeeDialog.add(lastNameField, gbc);

		// SSN
		gbc.gridx = 0;
		gbc.gridy = 3;
		addEmployeeDialog.add(new JLabel("SSN:"), gbc);
		gbc.gridx = 1;
		addEmployeeDialog.add(ssnField, gbc);

		// Birthdate
		gbc.gridx = 0;
		gbc.gridy = 4;
		addEmployeeDialog.add(new JLabel("Birthdate:"), gbc);
		gbc.gridx = 1;
		addEmployeeDialog.add(birthdateField, gbc);

		// Address
		gbc.gridx = 0;
		gbc.gridy = 5;
		addEmployeeDialog.add(new JLabel("Address:"), gbc);
		gbc.gridx = 1;
		addEmployeeDialog.add(addressField, gbc);

		// Sex
		gbc.gridx = 0;
		gbc.gridy = 6;
		addEmployeeDialog.add(new JLabel("Sex:"), gbc);
		gbc.gridx = 1;
		addEmployeeDialog.add(sexField, gbc);

		// Salary
		gbc.gridx = 0;
		gbc.gridy = 7;
		addEmployeeDialog.add(new JLabel("Salary:"), gbc);
		gbc.gridx = 1;
		addEmployeeDialog.add(salaryField, gbc);

		// Super_ssn
		gbc.gridx = 0;
		gbc.gridy = 8;
		addEmployeeDialog.add(new JLabel("Super_ssn:"), gbc);
		gbc.gridx = 1;
		addEmployeeDialog.add(superSsnField, gbc);

		// Dno
		gbc.gridx = 0;
		gbc.gridy = 9;
		addEmployeeDialog.add(new JLabel("Dno:"), gbc);
		gbc.gridx = 1;
		addEmployeeDialog.add(dnoField, gbc);

		// 버튼은 하단 중앙에 배치
		gbc.gridx = 0;
		gbc.gridy = 10;
		gbc.gridwidth = 2;  // 버튼이 두 열을 차지하도록 설정
		gbc.anchor = GridBagConstraints.CENTER;  // 버튼을 중앙에 배치
		addEmployeeDialog.add(addBtn, gbc);

		addBtn.addActionListener(e -> {
			// 데이터베이스에 새 직원 정보 삽입
			try {
				String query = "INSERT INTO company.Employee (Fname, Minit, Lname, Ssn, Bdate, Address, Sex, Salary, Super_ssn, Dno) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
				PreparedStatement pstmt = conn.prepareStatement(query);
				pstmt.setString(1, firstNameField.getText());
				pstmt.setString(2, middleInitField.getText());
				pstmt.setString(3, lastNameField.getText());
				pstmt.setString(4, ssnField.getText());
				pstmt.setString(5, birthdateField.getText());
				pstmt.setString(6, addressField.getText());
				pstmt.setString(7, (String) sexField.getSelectedItem());
				pstmt.setDouble(8, Double.parseDouble(salaryField.getText()));
				pstmt.setString(9, superSsnField.getText());
				pstmt.setInt(10, Integer.parseInt(dnoField.getText()));

				int rows = pstmt.executeUpdate();
				if (rows > 0) {
					JOptionPane.showMessageDialog(this, "새 직원이 추가되었습니다.");
					addEmployeeDialog.dispose();
					searchBtn.doClick(); // 직원 추가 후 검색 버튼을 자동으로 눌러 목록 갱신
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(this, "직원 추가 중 오류가 발생했습니다.");
			}
		});

		addEmployeeDialog.pack();
		addEmployeeDialog.setLocationRelativeTo(this);
		addEmployeeDialog.setVisible(true);
	}

	public static void main(String[] args) {
		new company();

	}

}
