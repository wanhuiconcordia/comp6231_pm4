
public class Student implements ReturnComparator<Student> {

	public String name;
	public int age;
	
	public Student(String name, int age){
		this.name = name;
		this.age = age;
	}

	@Override
	public boolean compare(Student otherStudent) {
		return name.equals(otherStudent.name) && age == otherStudent.age;
	}
}
