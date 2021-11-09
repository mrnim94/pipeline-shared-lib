package com.acceleratedskillup;

class Car {
  int numberOfDoors
  String brand
  String model
  String a
  def script
  
	Car(script) {
		this.script = script
		this.a = script.getClass()
	}
	
  	void printOutCar() {
		script.echo "using script.echo"
  		script.echo "numberOfDoors:${numberOfDoors}"    	
		script.echo "brand:${brand}"    	
		script.echo "model:${model}"
		script.echo "a:${a}"
  	}  
  
}
