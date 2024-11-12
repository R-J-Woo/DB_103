package jdbc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import java.sql.*;
import java.util.*;

public class company extends JFrame implements ActionListener {

	private Connection conn;

	private JLabel dbnameLabel = new JLabel("DB명: ");
	private JLabel userLabel = new JLabel("user: ");
	private JLabel pwdLabel = new JLabel("password: ");
	private JLabel searchLabel = new JLabel("검색 항목: ");
	private JLabel selectedEmpLabel = new JLabel("선택한 직원: ");
	private JLabel empCountLabel = new JLabel("인원 수: ");
	private JLabel empCount = new JLabel("0");

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
	private JButton deleteBtn = new JButton("선택한 데이터 삭제");
	private JButton addEmployeeBtn = new JButton("직원 추가");

	private JTable resultTable;
	private DefaultTableModel resultModel;
	private JScrollPane scrollPane;

	private ArrayList<String> columnNames = new ArrayList<String>();

	//조건 검색 관련 변수
	private JComboBox<String> conditionComboBox = new JComboBox<>(new String[] { "전체", "부서", "성별", "연봉" });
	private JComboBox<String> departmentComboBox = new JComboBox<>(
			new String[] { "Research", "Headquarters", "Administration" });
	private JComboBox<String> sexComboBox = new JComboBox<>(new String[] { "M", "F" });
	private JTextField salaryTextField = new JTextField(10);
	private JComboBox<String> groupConditionComboBox = new JComboBox<>(new String[] { "그룹 없음", "성별", "부서", "상급자" });

	// update 관련 변수
	private JLabel updateLabel = new JLabel("수정: ");
	private JComboBox<String> updateComboBox = new JComboBox<> (new String[] {"없음", "Name", "Ssn", "BDate", "Address", "Sex", "Salary", "Supervisor", "Department"});
	private JButton updateBtn = new JButton("UPDATE");
	private JTextField updateNameTextField = new JTextField(10);
	private JTextField updateSsnTextField = new JTextField(10);
	private JTextField updateBdateTextField = new JTextField(10);
	private JTextField updateAddressTextField = new JTextField(10);
	private JTextField updateSalaryTextField = new JTextField(10);
	private JComboBox<String> updateSexComboBox = new JComboBox<>(new String[]{"M", "F"}); 
	private JComboBox<String> updateSupervisorComboBox = new JComboBox<>(new String[]{});
	private JComboBox<String> updateDepartmentComboBox = new JComboBox<>(new String[]{"Research", "Headquarters", "Administration"});

	private ArrayList<String> selectedNameList = new ArrayList<String>();
	private ArrayList<String> selectedSsnList = new ArrayList<String>();
	
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
		searchPanel.add(searchBtn); // 검색 버튼
		searchPanel.add(nameText);
		searchPanel.add(ssnText);
		searchPanel.add(deleteBtn); // 직원 삭제 버튼
		searchPanel.add(addEmployeeBtn); // 직원 추가 버튼

		// 결과창 관련 코드
		JPanel resultPanel = new JPanel();
		resultPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
		resultModel = new DefaultTableModel();
		resultTable = new JTable(resultModel);
		scrollPane = new JScrollPane(resultTable);
		scrollPane.setPreferredSize(new Dimension(1000, 400));

		resultPanel.add(scrollPane);

		
		JPanel conditionPanel = new JPanel();
		conditionPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
		conditionPanel.add(new JLabel("검색 범위:"));
		conditionPanel.add(conditionComboBox);
		conditionPanel.add(departmentComboBox);
		conditionPanel.add(sexComboBox);
		conditionPanel.add(salaryTextField);

		// 기본적으로 부서, 성별, 연봉 입력 필드는 비활성화 또는 숨김 상태
		departmentComboBox.setVisible(false);
		sexComboBox.setVisible(false);
		salaryTextField.setVisible(false);

		// 그룹별 평균 월급 조건 패널 설정
		JPanel groupConditionPanel = new JPanel();
		groupConditionPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
		groupConditionPanel.add(new JLabel("그룹별 평균 월급:"));
		groupConditionPanel.add(groupConditionComboBox);

		JPanel ConditionPanel = new JPanel();
		ConditionPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
		ConditionPanel.add(conditionPanel);
		ConditionPanel.add(groupConditionPanel);

		// 하나의 큰 Top panel에 DB 연결, 검색, 조건 선택, 결과창 panel을 집어넣음
		JPanel Top = new JPanel();
		Top.setLayout(new BoxLayout(Top, BoxLayout.Y_AXIS));
		Top.add(dbConnPanel);
		Top.add(searchPanel);
		Top.add(ConditionPanel);
		Top.add(resultPanel);

		// 선택한 직원
		JPanel selectedEmpPanel = new JPanel();
		selectedEmpPanel.setLayout(new BoxLayout(selectedEmpPanel, BoxLayout.Y_AXIS));
		selectedEmpLabel.setBorder(new EmptyBorder(10, 5, 10, 5));
		empCountLabel.setBorder(new EmptyBorder(10, 5, 10, 5));
		selectedEmpPanel.add(selectedEmpLabel);
		selectedEmpPanel.add(empCountLabel);

		JPanel Middle = new JPanel();
		Middle.setLayout(new BoxLayout(Middle, BoxLayout.Y_AXIS));
		Middle.add(selectedEmpPanel);
		
		// 직원 수정
		JPanel updatePanel = new JPanel();
		updatePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
		updatePanel.add(updateLabel);
		updatePanel.add(updateComboBox);
		updatePanel.add(updateNameTextField);
		updatePanel.add(updateSsnTextField);
		updatePanel.add(updateBdateTextField);
		updatePanel.add(updateAddressTextField);
		updatePanel.add(updateSalaryTextField);
		updatePanel.add(updateSexComboBox);
		updatePanel.add(updateSupervisorComboBox);
		updatePanel.add(updateDepartmentComboBox);
		updatePanel.add(updateBtn);
		
		updateNameTextField.setVisible(false);
		updateSsnTextField.setVisible(false);
		updateBdateTextField.setVisible(false);
		updateAddressTextField.setVisible(false);
		updateSalaryTextField.setVisible(false);
		updateSexComboBox.setVisible(false);
		updateSupervisorComboBox.setVisible(false);
		updateDepartmentComboBox.setVisible(false);
		
		JPanel Bottom = new JPanel();
		Bottom.setLayout(new BoxLayout(Bottom, BoxLayout.Y_AXIS));
		Bottom.add(updatePanel);

		add(Top, BorderLayout.NORTH);
		add(Middle, BorderLayout.CENTER);
		add(Bottom, BorderLayout.SOUTH);

		// 버튼과 이벤트 처리 함수 연결
		connBtn.addActionListener(this);
		searchBtn.addActionListener(this);
		deleteBtn.addActionListener(this);
		addEmployeeBtn.addActionListener(e -> openAddEmployeeDialog());
		updateBtn.addActionListener(this);


		updateComboBox.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        String updateCondition = (String) updateComboBox.getSelectedItem();

		        // 모든 입력 필드를 숨깁니다
				updateNameTextField.setVisible(false);
				updateSsnTextField.setVisible(false);
				updateBdateTextField.setVisible(false);
				updateAddressTextField.setVisible(false);
				updateSalaryTextField.setVisible(false);
				updateSexComboBox.setVisible(false);
				updateSupervisorComboBox.setVisible(false);
				updateDepartmentComboBox.setVisible(false);
				
		        // 선택된 조건에 따라 필드를 표시합니다
		        if ("Name".equals(updateCondition)) {
		        	updateNameTextField.setVisible(true);
		        } else if ("Ssn".equals(updateCondition)) {
		        	updateSsnTextField.setVisible(true);
		        } else if ("BDate".equals(updateCondition)) {
		        	updateBdateTextField.setVisible(true);
		        } else if ("Address".equals(updateCondition)) {
		        	updateAddressTextField.setVisible(true);
		        } else if ("Sex".equals(updateCondition)) {
		        	updateSexComboBox.setVisible(true);
		        } else if ("Salary".equals(updateCondition)) {
		        	updateSalaryTextField.setVisible(true);
		        } else if ("Supervisor".equals(updateCondition)) {
		        	setSupervisor();
		        	updateSupervisorComboBox.setVisible(true);
		        } else if ("Department".equals(updateCondition)) {
		        	updateDepartmentComboBox.setVisible(true);
		        }

		        // 패널을 다시 그려서 변경 사항이 반영되도록 합니다
		        updatePanel.revalidate();
		        updatePanel.repaint();
		    }
		});

		
		conditionComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String selectedCondition = (String) conditionComboBox.getSelectedItem();

				// 모든 입력 필드를 숨김
				departmentComboBox.setVisible(false);
				sexComboBox.setVisible(false);
				salaryTextField.setVisible(false);

				// 선택된 조건에 따라 필드를 표시
				if ("부서".equals(selectedCondition)) {
					departmentComboBox.setVisible(true);
				} else if ("성별".equals(selectedCondition)) {
					sexComboBox.setVisible(true);
				} else if ("연봉".equals(selectedCondition)) {
					salaryTextField.setVisible(true);
				}

				// 패널을 다시 그려서 변경 사항이 반영
				conditionPanel.revalidate();
				conditionPanel.repaint();
			}
		});

		setSize(1300, 800); // 창 크기 세팅
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

				// 유저의 grant 확인
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("SHOW GRANTS FOR CURRENT_USER");

				boolean canInsert = false;
				boolean canDelete = false;

				while (rs.next()) {
				    String grant = rs.getString(1);
				    String safeGrant = grant.split(" ON ")[0]; // grant 문자열에서 ON 이전 부분만 추출 -> 
				    					       //보안 문제(유저명이 DELETE 거나 INSERT면 권한이 없어도 기능 사용 가능

				    if (safeGrant.contains("INSERT")) {
				        canInsert = true;
				    }
				    if (safeGrant.contains("DELETE")) {
				        canDelete = true;
				    }
				}

				// 권한에 따라 버튼 활성화
				addEmployeeBtn.setEnabled(canInsert);
				deleteBtn.setEnabled(canDelete);

			} catch (SQLException e1) {
				System.err.println("DB 연결에 실패했습니다.");
				connBtn.setForeground(Color.red);
				connBtn.setText("연결 실패");
				e1.printStackTrace();
			}
		}

		if (e.getSource() == searchBtn) { // 검색 버튼이 클릭되었을 때

			selectedNameList.clear();
			selectedSsnList.clear();
			
			String query = getQuery(); // select 하는 쿼리 가져오기

			if (!"그룹 없음".equals(groupConditionComboBox.getSelectedItem())) {
				query = getGroupQuery(); // 그룹별 평균 월급 및 검색 조건이 적용된 쿼리

				// 그룹별 평균 월급에 따라 열 이름을 설정합니다.
				columnNames.clear();
				String selectedGroup = (String) groupConditionComboBox.getSelectedItem();
				if ("부서".equals(selectedGroup)) {
					columnNames.add("Dname"); // 부서명을 표시하는 열 이름
				} else if ("성별".equals(selectedGroup)) {
					columnNames.add("SEX"); // 성별을 표시하는 열 이름
				} else if ("상급자".equals(selectedGroup)) {
					columnNames.add("Supervisor"); // 상급자 이름을 표시하는 열 이름
				}
				columnNames.add("AVG_Salary"); // 평균 월급 표시
			} else {
				query = getConditionsQuery(); // where 조건 추가 검색
			}

			// 평균 급여 그룹이 없으면 선택 column 생성, 그룹이 있으면 선택 column 생성하지 않음
			if ("그룹 없음".equals(groupConditionComboBox.getSelectedItem())) {
				resultModel = new DefaultTableModel(columnNames.toArray(), 0) {
					@Override
					public Class<?> getColumnClass(int columnIndex) {
						if (columnIndex == 0)
							return Boolean.class;
						return super.getColumnClass(columnIndex);
					}
				};

				resultModel.addTableModelListener(new TableModelListener() {

					@Override
					public void tableChanged(TableModelEvent e) {

						if (e.getType() == TableModelEvent.UPDATE) {
							int row = e.getFirstRow();
							int col = e.getColumn();

							if (col == 0) {
								Boolean isChecked = (Boolean) resultModel.getValueAt(row, col);
								String name = (String) resultModel.getValueAt(row, 1);
								String ssn = (String) resultModel.getValueAt(row, 2);
								if (isChecked) {
									selectedNameList.add(name); // 선택한 직원에 추가
									selectedSsnList.add(ssn);	// 선택한 직원에 추가
								} else {
									selectedNameList.remove(name); // 선택한 직원에서 제거
									selectedSsnList.remove(ssn);	// 선택한 직원에서 제거
								}

								setSelectedEmp();
								revalidate();
								repaint();
							}
						}
					}
				});
			} else {
				resultModel = new DefaultTableModel(columnNames.toArray(), 0);
			}

			if (query.length() > 0) {
				try {
					Statement stmt = conn.createStatement();
					ResultSet rs = stmt.executeQuery(query);
					int colCount = rs.getMetaData().getColumnCount(); // 열의 갯수 체크
					int rowCount = 0;

					while (rs.next()) {
						ArrayList<Object> row = new ArrayList<>();

						if ("그룹 없음".equals(groupConditionComboBox.getSelectedItem())) {
							row.add(false);
						}

						for (int i = 1; i <= colCount; i++) {
							row.add(rs.getObject(i));
						}

						rowCount += 1;
						resultModel.addRow(row.toArray());
					}

					resultTable = new JTable(resultModel);
					scrollPane.setViewportView(resultTable);
					selectedEmpLabel.setText("선택한 직원: ");
					empCountLabel.setText("인원 수: " + rowCount);
					revalidate();
					repaint();
				} catch (SQLException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null, "DB 검색 중 에러가 발생했습니다.");
				}
			} else {
				JOptionPane.showMessageDialog(null, "선택한 항목이 없습니다.");
			}
		}

		if (e.getSource() == deleteBtn) { // 삭제 버튼 클릭 시
			deleteEmployee();
			searchBtn.doClick(); // 검색 버튼을 자동으로 눌러 목록 갱신
		}
		
		if (e.getSource() == updateBtn) { // update 버튼 클릭 시
			updateEmployee();
			searchBtn.doClick(); // 검색 버튼을 자동으로 눌러 목록 갱신
		}
	}

	private void setSelectedEmp() {
		selectedEmpLabel.setText("선택한 직원: ");

		for (String name : selectedNameList) {
			selectedEmpLabel.setText(selectedEmpLabel.getText() + name + " ");
		}
	}
	
	private void setSupervisor() {
		try {
			String query = "select CONCAT(Fname, ' ', Minit, ' ', Lname) from employee";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			while (rs.next()) {
				updateSupervisorComboBox.addItem(rs.getObject(1) + "");
			}
			
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	// 검색 쿼리 가져오는 함수
	private String getQuery() {
		int selectedCount = 0;
		String query = "select ";
		columnNames.clear();
		columnNames.add("선택");

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
			query += "(select CONCAT(Fname, ' ', Minit, ' ', Lname) from Employee where ssn = E.Super_ssn)";
			selectedCount += 1;
			columnNames.add("SUPERVISOR");
		}

		if (departmentCB.isSelected()) {
			if (selectedCount != 0) {
				query += ",";
			}
			query += "(select Dname from department where Dnumber = E.Dno)";
			selectedCount += 1;
			columnNames.add("DEPARTMENT");
		}

		query += " from employee E";

		if (selectedCount == 0) {
			query = "";
		}

		return query;
	}

	private void updateEmployee() {
		
		String updateQuery = getUpdateQuery();
		
		if (updateQuery.length() > 0) {
			try {
				Statement stmt = conn.createStatement();
				int updatedRowCount = stmt.executeUpdate(updateQuery);
	            if (updatedRowCount > 0) {
	                System.out.println("업데이트 완료!");
	                JOptionPane.showMessageDialog(null, "선택된 직원들이 업데이트 되었습니다.");
	            } else {
	                JOptionPane.showMessageDialog(null, "업데이트된 직원이 없습니다.");
	            }
			}
			catch (SQLException e1) {
				e1.printStackTrace();
		        JOptionPane.showMessageDialog(null, "DB 업데이트 중 에러가 발생했습니다.");
			}
		} else {
	        JOptionPane.showMessageDialog(null, "선택한 직원이 없습니다.");
		}
	}
	
	private void deleteEmployee() {
		// 사용자가 선택한 조건을 기반으로 DELETE 쿼리 생성
		String query = getDeleteQuery();

		if (query.length() > 0) { // 조건이 있을 때만 실행
			try {
				Statement stmt = conn.createStatement();
				int rowsAffected = stmt.executeUpdate(query); // DELETE 쿼리 실행
				if (rowsAffected > 0) {
					System.out.println("삭제 완료!");
					JOptionPane.showMessageDialog(null, "입력한 직원이 삭제되었습니다.");
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

	private String getUpdateQuery() {
		String query = "update employee set ";

        String updateCondition = (String) updateComboBox.getSelectedItem();
        
        // set 절
        if ("Name".equals(updateCondition)) {
        	String[] name = updateNameTextField.getText().split(" ");
        	String fname = name[0];
        	String minit = name[1];
        	String lname = name[2];
        	query += "fname = '" + fname + "'";
        	query += ", Minit = '" + minit + "'";
        	query += ", Lname = '" + lname + "'";
        	
        } else if ("Ssn".equals(updateCondition)) {
        	query += "ssn = " + updateSsnTextField.getText().toString();
        } else if ("BDate".equals(updateCondition)) {
        	query += "Bdate = '" + updateBdateTextField.getText().toString() + "'";
        } else if ("Address".equals(updateCondition)) {
        	query += "Address = '" + updateAddressTextField.getText().toString() + "'";
        } else if ("Sex".equals(updateCondition)) {
        	query += "Sex = '" + updateSexComboBox.getSelectedItem().toString() + "'";
        } else if ("Salary".equals(updateCondition)) {
        	query += "Salary = " + updateSalaryTextField.getText().toString();
        } else if ("Supervisor".equals(updateCondition)) {
        	String superVisorSsn = getSupervisorSsn(updateSupervisorComboBox.getSelectedItem().toString());
        	query += "Super_ssn = " + superVisorSsn;
        } else if ("Department".equals(updateCondition)) {
        	query += "Dno = (select Dnumber from department where Dname = '" + updateDepartmentComboBox.getSelectedItem() + "')";
        }
        
        query += ", modified = CURRENT_TIMESTAMP()";
        
        // where 절
        query += " where ssn in (";
        
        for (int i = 0; i < selectedSsnList.size(); i++) {
        	query += selectedSsnList.get(i);
        	if (i < selectedSsnList.size() - 1) {
        		query += ", ";
        	}
        }
        
        query += ")";
        
        if (selectedSsnList.size() == 0) {
        	query = "";
        }
		
		return query;
	}

	private String getSupervisorSsn(String superVisorName) {

		String superVisorSsn = "";
		
		try {
			String query = "select ssn from employee where CONCAT(Fname, ' ', Minit, ' ', Lname) = '" + superVisorName + "'";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);

			while (rs.next()) {
				superVisorSsn = rs.getObject(1) + "";
			}
			
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		return superVisorSsn;
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
			if (whereClauseAdded)
				query += " AND ";
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
	//검색 범위 쿼리 생성
	private String getConditionsQuery() {
		String query = getQuery();
		String condition = "";

		String selectedCondition = (String) conditionComboBox.getSelectedItem();
		if ("부서".equals(selectedCondition)) {
			condition += " E.Dno = (SELECT Dnumber FROM department WHERE Dname = '"
					+ departmentComboBox.getSelectedItem() + "')";
		} else if ("성별".equals(selectedCondition)) {
			condition += "sex = '" + sexComboBox.getSelectedItem() + "'";
		} else if ("연봉".equals(selectedCondition) && !salaryTextField.getText().isEmpty()) {
			condition += "salary >= " + salaryTextField.getText();
		}

		if (!condition.isEmpty()) {
			query += " WHERE " + condition;
		}

		return query;
	}
	//그룹별 평균 월급 쿼리 생성
	private String getGroupQuery() {
		String selectedGroup = (String) groupConditionComboBox.getSelectedItem();
		String query = "";
		String whereClause = "";

		// 기본 쿼리 생성
		if ("부서".equals(selectedGroup)) {
			query = "SELECT D.Dname, AVG(E.salary) FROM employee E " + "JOIN department D ON E.Dno = D.Dnumber ";
		} else if ("상급자".equals(selectedGroup)) {
			query = "SELECT CONCAT(S.Fname, ' ', S.Minit, ' ', S.Lname), AVG(E.salary)"
					+ "FROM employee E JOIN employee S ON E.Super_ssn = S.ssn ";
		} else if ("성별".equals(selectedGroup)) {
			query = "SELECT E.sex, AVG(E.salary) FROM employee E ";
		}

		// 검색 범위 조건 추가(검색 범위 조건이 있으면)
		String selectedCondition = (String) conditionComboBox.getSelectedItem();
		if ("부서".equals(selectedCondition)) {
			whereClause = "E.Dno = (SELECT Dnumber FROM department WHERE Dname = '"
					+ departmentComboBox.getSelectedItem() + "')";
		} else if ("성별".equals(selectedCondition)) {
			whereClause = "E.sex = '" + sexComboBox.getSelectedItem() + "'";
		} else if ("연봉".equals(selectedCondition) && !salaryTextField.getText().isEmpty()) {
			whereClause = "E.salary >= " + salaryTextField.getText();
		}

		// WHERE 절이 있다면 추가
		if (!whereClause.isEmpty()) {
			query += "WHERE " + whereClause + " ";
		}

		// GROUP BY 절 추가
		if ("부서".equals(selectedGroup)) {
			query += "GROUP BY D.Dname";
		} else if ("상급자".equals(selectedGroup)) {
			query += "GROUP BY E.Super_ssn";
		} else if ("성별".equals(selectedGroup)) {
			query += "GROUP BY E.sex";
		}

		return query;
	}

	private void openAddEmployeeDialog() {
		JDialog addEmployeeDialog = new JDialog(this, "새로운 직원 정보 추가", true);
		String[] genders = { "F", "M" };
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
		gbc.gridwidth = 2; // 버튼이 두 열을 차지하도록 설정
		gbc.anchor = GridBagConstraints.CENTER; // 버튼을 중앙에 배치
		addEmployeeDialog.add(addBtn, gbc);

		addBtn.addActionListener(e -> {
			// 데이터베이스에 새 직원 정보 삽입
			try {
				String query = "INSERT INTO Employee (Fname, Minit, Lname, Ssn, Bdate, Address, Sex, Salary, Super_ssn, Dno, created, modified) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP())";
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
