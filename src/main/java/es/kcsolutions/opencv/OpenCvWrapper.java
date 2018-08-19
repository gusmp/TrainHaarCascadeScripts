package es.kcsolutions.opencv;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class OpenCvWrapper {
	
	
	public OpenCvWrapper() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	
	public Mat leerImagen(String imageName) {
		return Imgcodecs.imread(imageName);
	}
	
	public Mat recortarImagen(Mat imagen, int x_ini, int y_ini, int x_fin, int y_fin ) {
		Rect areaInteres = new Rect(x_ini, y_ini ,x_fin, y_fin);
		return imagen.submat(areaInteres);
	}
	
	public Mat recortarImagen(Mat imagen, int x_ini, int y_ini) {
		Rect areaInteres = new Rect(x_ini, y_ini ,imagen.cols() - x_ini, imagen.rows() - y_ini);
		return imagen.submat(areaInteres);
	}
	
	public void guardarImagen(Mat imagen, String nombreArchivo) {
		Imgcodecs.imwrite(nombreArchivo, imagen);
	}
	
	public Mat convertirGris(Mat imagen) {
		Mat imagenGris = new Mat();
		Imgproc.cvtColor(imagen, imagenGris, Imgproc.COLOR_RGB2GRAY);
		return imagenGris;
	}
	
	public Mat contrastar(Mat imagen) {
		Mat destination = new Mat(imagen.rows(),imagen.cols(),imagen.type());
		Imgproc.GaussianBlur(imagen, destination, new Size(0,0), 10);
        Core.addWeighted(imagen, 1.5, destination, -0.5, 0, destination);
        return destination;
	}
	
	public Mat dilatar(Mat imagen, int tamanyo) {

		Mat elementoDilatador = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new  Size(2*tamanyo + 1, 2*tamanyo+1));
        Mat imagenDilate = new Mat();
        Imgproc.dilate(imagen, imagenDilate, elementoDilatador);
        return imagenDilate;
	}
	
	public Mat invertir(Mat imagen) {
		Mat inv = imagen.clone();
		Core.bitwise_not(inv, inv);
		return inv;
	}
	
	public Mat difuminar(Mat imagen, int tamanyo) {
        Mat blur = new Mat();
		Imgproc.GaussianBlur(imagen, blur, new Size(tamanyo, tamanyo), 1.0);
		return blur;
	}
	
	public Mat obtenerContornos(Mat imagen, int threshold_inferior, int threshold_superior) {
		Mat canny = new Mat();
		Imgproc.Canny(imagen, canny, threshold_inferior, threshold_superior);
		return canny;
	}
	
	public List<MatOfPoint> obtenerListaContornos(Mat imagenContornos) {
		List<MatOfPoint> contornos = new ArrayList<MatOfPoint>();
		Imgproc.findContours(imagenContornos, contornos, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
		return contornos;
	}
	
	public Mat generaImagenConContornos(Mat imagen, List<MatOfPoint> listaContornos) {
		
		Mat imagenConContornos = imagen.clone();
		Random rnd = new Random(System.currentTimeMillis());
		for(int i=0; i < listaContornos.size(); i++) {
			Scalar color = new Scalar(rnd.nextInt(255) , rnd.nextInt(255), rnd.nextInt(255));
			Imgproc.drawContours(imagenConContornos, listaContornos, i, color);
			RotatedRect rectanguloContorno = Imgproc.minAreaRect(new MatOfPoint2f(listaContornos.get(i).toArray()));
			Imgproc.putText(imagenConContornos, "" + i , new Point(rectanguloContorno.center.x , rectanguloContorno.center.y), Core.FONT_HERSHEY_PLAIN, 1.0, new Scalar(0,0,0));
		}
		
		return imagenConContornos;
	}
	
	public Mat thresholdAdaptativo(Mat imagen, int tamanoBloque) {
		Mat threshold = new Mat();
		Imgproc.adaptiveThreshold(imagen, threshold, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, tamanoBloque, 0);
		return threshold;
	} 
	
	public Mat closing(Mat imagen, int tamanyo) {

		Mat elementDilate = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new  Size(2*tamanyo+1 , 2*tamanyo+1));
        Mat closing = new Mat();
        Imgproc.morphologyEx(imagen, closing, Imgproc.MORPH_CLOSE, elementDilate);
        return closing;
	}
	
	public Mat opening(Mat imagen, int tamanyo) {

		Mat elementDilate = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new  Size(2*tamanyo+1 , 2*tamanyo+1));
        Mat opening = new Mat();
        Imgproc.morphologyEx(imagen, opening, Imgproc.MORPH_OPEN, elementDilate);
        return opening;
	}
	
	public Mat thresholdOtsu(Mat imagen, double threshold) {
		
		Mat thresholdMat = new Mat();
		Imgproc.threshold(imagen, thresholdMat, 255-threshold, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
		return thresholdMat;
	}
	
	public Mat threshold(Mat imagen, double threshold) {
		
		Mat thresholdMat = new Mat();
		Imgproc.threshold(imagen, thresholdMat, 255-threshold, 255, Imgproc.THRESH_BINARY);
		return thresholdMat;
	}
	
	public Mat zoom(Mat imagen, int factor) {
		
		Mat zoom = new Mat(imagen.rows()*factor, imagen.cols()*factor, imagen.type());
		//Imgproc.resize(imagen, zoom, zoom.size(), 0, 0, Imgproc.INTER_CUBIC);
		Imgproc.resize(imagen, zoom, zoom.size(), 0, 0, Imgproc.INTER_LANCZOS4);
		return zoom;
	}
	
	/*
	public Mat sobel(Mat imagen) {
		Mat sobel = new Mat();
		Imgproc.Sobel(imagen, sobel, Imgproc., 1, 0);
		return sobel;
	}
	*/
	
	public Mat rotar(Mat imagen,double grados) {
		
		Mat resultado = new Mat();
		
		/*
		Mat rotacion = new Mat(2,3,CvType.CV_32F);
		rotacion.put(0, 0, Math.cos(grados));
		rotacion.put(0, 1, Math.sin(grados));
		rotacion.put(0, 2, 0);
		
		rotacion.put(1, 0, -Math.sin(grados));
		rotacion.put(1, 1, Math.cos(grados));
		rotacion.put(1, 2, 0);
		
		Imgproc.warpAffine(imagen, resultado, rotacion, imagen.size());
		return resultado;
		*/
		Point centro = new Point(imagen.cols()/2, imagen.rows()/2);
		Mat rotacion= Imgproc.getRotationMatrix2D(centro, grados, 1.0);
		
		Imgproc.warpAffine(imagen, resultado, rotacion, imagen.size());
	    
		return resultado;
		
	}

}
