package hr.fer.zemris.java.hw16.trazilica.util;

import java.util.Objects;
import static java.lang.Math.pow;

/**
 * Razred koji implemenira model vektora u 3D sustavu sa koeficijentima x,z,i y.
 * Također sadrži neke osnovne metode za rad sa vektorima
 * 
 * @author Mihael
 *
 */
public class Vector3 {

	private double[] coordinates;

	/**
	 * Constructor initializes new vector
	 * 
	 * @param coordinates
	 *            - coordinates
	 */
	public Vector3(double... coordinates) {
		this.coordinates = coordinates;
	}

	/**
	 * Method returns vector module
	 * 
	 * @return vector module
	 */
	public double norm() {
		double sum = 0;

		for (double number : coordinates) {
			sum += pow(number, 2);
		}

		return sum;
	}

	/**
	 * Metpda koja skalrano(sam sa sobom) množi vektor
	 * 
	 * @param other
	 *            - drugi kompleksni broj
	 * @return skalarni oblik proizvodnje
	 * 
	 * @throws NullPointerException
	 *             - ako je argument <code>null</code>
	 */
	public double dot(Vector3 other) {
		Objects.requireNonNull(other);
		double sum = 0;
		double[] coordinatesOther = other.toArray();

		for (int i = 0; i < coordinates.length; i++) {
			sum += coordinates[i] * coordinatesOther[i];
		}

		return sum;
	}

	/**
	 * Metoda vraća kosinus kuta između trenutnog vektora i argumenta
	 * 
	 * @param other
	 *            - atgument
	 * @return kosinus kuta
	 */
	public double cosAngle(Vector3 other) {
		return dot(other) / (norm() * other.norm());
	}

	/**
	 * Metoda stvara novo polje sa koeficijentima
	 * 
	 * @return novo polje koeficijenata
	 */
	public double[] toArray() {
		return coordinates;
	}

	public static Vector3 multiply(double[] vector1, double[] vector2) {
		if (vector1.length != vector2.length) {
			throw new IllegalArgumentException("Vectors must have same numebr of coeffcients,but one has "
					+ vector1.length + ",and other " + vector2.length);
		}

		double[] array = new double[vector1.length];

		for (int i = 0, size = vector1.length; i < size; i++) {
			array[i] = vector1[i] * vector2[i];
		}

		return new Vector3(array);
	}
}
