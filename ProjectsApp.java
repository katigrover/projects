package projects;

import java.math.BigDecimal;

import java.sql.Connection;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

public class ProjectsApp {
	private Scanner scanner = new Scanner(System.in);
	private ProjectService projectService = new ProjectService();

	// @formatter:off
	private List<String> operations = List.of(
			"1) Add a project",
			"2) List projects",
			"3) Select a project", 
			"4) Update project details", 
			"5) Delete a project"
	);
	// @formatter:on
	private Object curProject;

	public static void main(String[] args) {
		new ProjectsApp().processUserSelections();
	}

	private void processUserSelections() {
		boolean done = false;

		while (!done) {
			try {
				int selection = getUserSelection();

				switch (selection) {
				case -1:
					done = exitMenu();
					break;

				case 1:
					createProject();
					break;

				case 2:
					listProjects();
					break;

				case 3:
					selectProject();
					break;

				case 4:
					updateProjectDetails();
					break;

				case 5:
					deleteProject();

				default:
					System.out.println("\n" + selection + " is not a valid selection. Try again.");
				}
			} catch (Exception e) {
				System.out.println("\nError: " + e + " Try again.");
				System.out.println();
			}
		}
	}

	private void deleteProject() {
		listProjects();
		
		Integer projectId = getIntInput("Enter the project ID to delete");
		
		projectService.deleteProject(projectId);
		
		System.out.println("\nProject " + projectId + " was successfully deleted.");
		
		if(Objects.nonNull(curProject) && ((Project) curProject).getProjectId().equals(projectId)) {
			curProject = null;
		}
	}

	private void updateProjectDetails() {
		
		if(Objects.isNull(curProject)) {
			System.out.println("\nPlease select a project.");
			return;
		}
		
		String projectName = getStringInput("\nEnter the project name [" + ((Project) curProject).getProjectName() + "]");

		BigDecimal estimatedHours = getDecimalInput("\nEnter the estimated hours [" + ((Project) curProject).getEstimatedHours() + "]");

		BigDecimal actualHours = getDecimalInput("\nEnter the actual hours [" + ((Project) curProject).getActualHours() + "]");

		Integer difficulty = getIntInput("\nEnter the project difficulty (1-5) [" + ((Project) curProject).getDifficulty() + "]");

		String notes = getStringInput("\nEnter the project notes [" + ((Project) curProject).getNotes() + "]");

		Project project = new Project();

		project.setProjectId(((Project) curProject).getProjectId());
		project.setProjectName(Objects.isNull(projectName) ? ((Project) curProject).getProjectName() : projectName);
		project.setEstimatedHours(Objects.isNull(estimatedHours) ? ((Project) curProject).getEstimatedHours() : estimatedHours);
		project.setActualHours(Objects.isNull(actualHours) ? ((Project) curProject).getActualHours() : actualHours);
		project.setDifficulty(Objects.isNull(difficulty) ? ((Project) curProject).getDifficulty() : difficulty);
		project.setNotes(Objects.isNull(notes) ? ((Project) curProject).getNotes() : notes);

		projectService.modifyProjectDetails(project);
		curProject = projectService.fetchByProjectId(((Project) curProject).getProjectId());
	}

	private void selectProject() {
		listProjects();

		Integer projectId = getIntInput("\nEnter a project ID to select a project");

		curProject = null;

		curProject = projectService.fetchByProjectId(projectId);

	}

	private void listProjects() {
		List<Project> projects = projectService.fetchAllProjects();

		System.out.println("\nProjects:");

		projects.forEach(
				project -> System.out.println("     " + project.getProjectId() + ": " + project.getProjectName()));
	}

	private void createProject() {
		String projectName = getStringInput("\nEnter the project name");
		BigDecimal estimatedHours = getDecimalInput("\nEnter the estimated hours");
		BigDecimal actualHours = getDecimalInput("\nEnter the actual hours");
		Integer difficulty = getIntInput("\nEnter the project difficulty (1-5)");
		String notes = getStringInput("\nEnter the project notes");

		Project project = new Project();

		project.setProjectName(projectName);
		project.setEstimatedHours(estimatedHours);
		project.setActualHours(actualHours);
		project.setDifficulty(difficulty);
		project.setNotes(notes);

		Project dbProject = projectService.addProject(project);
		System.out.println("\nYou have successfully created project: " + dbProject);
	}

	private BigDecimal getDecimalInput(String prompt) {
		String input = getStringInput(prompt);

		if (Objects.isNull(input)) {
			return null;
		}
		try {
			return new BigDecimal(input).setScale(2);
		} catch (NumberFormatException e) {
			throw new DbException(input + " is not a valid decimal number.");
		}
	}

	private boolean exitMenu() {
		System.out.println("\nExiting the menu.");
		return true;
	}

	private int getUserSelection() {
		printOperations();

		Integer input = getIntInput("\nEnter a menu selection");

		return Objects.isNull(input) ? -1 : input;
	}

	private void printOperations() {
		System.out.println("These are the available selections. Press the Enter key to quit:");

		operations.forEach(line -> System.out.println("   " + line));

		if (Objects.isNull(curProject)) {
			System.out.println("\nYou are not working with a project.");
		} else {
			System.out.println("\nYou are working with project: " + curProject);
		}
	}

	private Integer getIntInput(String prompt) {
		String input = getStringInput(prompt);

		if (Objects.isNull(input)) {
			return null;
		}
		try {
			return Integer.valueOf(input);
		} catch (NumberFormatException e) {
			throw new DbException(input + " is not a valid number.");
		}
	}

	private String getStringInput(String prompt) {
		System.out.print(prompt + ": ");

		String input = scanner.nextLine();

		return input.isBlank() ? null : input.trim();
	}

}
