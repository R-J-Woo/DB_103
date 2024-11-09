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
import javax.swing.JOptionPane;

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
	
	private JLabel totalEmp = new JLabel("인원수 : ");	
	final JLabel totalCount = new JLabel();	JPanel panel;	
	JScrollPane ScPane;	
	private JLabel Emplabel = new JLabel("선택한 직원: ");	
	private JLabel ShowSelectedEmp = new JLabel();	
	private JButton deleteBtn = new JButton("선택한 데이터 삭제");	
	int count = 0;
		
	private JTextField dbnameText = new JTextField(10);
	private JTextField userText = new JTextField(10);
	private JTextField pwdText = new JTextField(10);
	private JTextField nameText = new JTextField(10); // 이름 입력 필드
	private JTextField ssnText = new JTextField(10); // SSN 입력 필드

	
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
		searchPanel.add(nameText); 
		searchPanel.add(ssnText); 
		searchPanel.add(searchBtn);
		searchPanel.add(deleteBtn);
		
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
		deleteBtn.addActionListener(this);
		
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
		if (e.getSource() == deleteBtn) { // 삭제 버튼 클릭 시
	        deleteEmployee();
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
	
	// DETELE --------------------------------------------------------------------
	
	private void deleteEmployee() {
	    // 사용자가 선택한 조건을 기반으로 DELETE 쿼리 생성
	    String query = getDeleteQuery(); 

	    if (query.length() > 0) { // 조건이 있을 때만 실행
	        try {
	            Statement stmt = conn.createStatement();
	            int rowsAffected = stmt.executeUpdate(query); // DELETE 쿼리 실행
	            if (rowsAffected > 0) {
	                System.out.println("삭제 완료!");
	                JOptionPane.showMessageDialog(null, "선택된 직원들이 삭제되었습니다.");
	            } else {
	                JOptionPane.showMessageDialog(null, "삭제할 직원이 없습니다.");
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	            System.err.println("삭제 중 오류가 발생했습니다.");
	        }
	    } else {
	        JOptionPane.showMessageDialog(null, "삭제할 조건이 없습니다.");
	    }
	}

	// DELETE 쿼리 생성
	private String getDeleteQuery() {
	    int selectedCount = 0;
	    String query = "DELETE FROM company.employee WHERE ";
	    boolean whereClauseAdded = false;

	    // 선택된 조건에 맞게 쿼리 생성
	    if (nameCB.isSelected()) {
	        query += "CONCAT(Fname, ' ', Minit, ' ', Lname) LIKE '%" + nameText.getText() + "%'";
	        selectedCount++;
	        whereClauseAdded = true;
	    }

	    if (ssnCB.isSelected()) {
	        if (whereClauseAdded) query += " AND ";
	        query += "ssn = '" + ssnText.getText() + "'"; // 예시: SSN을 기준으로 삭제
	        selectedCount++;
	        whereClauseAdded = true;
	    }

	    // 다른 조건들을 추가 (생일, 주소 등)

	    if (selectedCount == 0) {
	        return ""; // 조건이 없으면 빈 문자열 반환
	    }

	    return query; // 완성된 DELETE 쿼리 반환
	}
	
	
	// DETELE --------------------------------------------------------------------


	public static void main(String[] args) {
		new company();

	}

}
