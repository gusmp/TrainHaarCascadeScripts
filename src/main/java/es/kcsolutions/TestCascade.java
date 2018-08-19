package es.kcsolutions;

import java.io.File;
import java.io.FilenameFilter;

import es.kcsolutions.opencv.OpenCvWrapper;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

public class TestCascade {

	public static void main(String[] args) {
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		String BASE_PATH = "c:/soft/dev/workspace_incubator/TrainHaarCascadeScripts/src/main/resources/";
		if (BASE_PATH.endsWith("/") == false) BASE_PATH += "/";
		
		String BASE_PATH_CASCADE= BASE_PATH + "cascade/";
		if (BASE_PATH_CASCADE.endsWith("/") == false) BASE_PATH_CASCADE += "/";
		 
		
		
		//String pathXml = BASE_PATH_CASCADE + "eu.xml";
		//String pathXml = BASE_PATH_CASCADE + "cascade_2018_02_05_mis_matriculas.xml";
		//String pathXml = BASE_PATH_CASCADE + "cascade_2018_02_16_carmen1.xml";
		//String pathXml = BASE_PATH_CASCADE + "cascade_2018_06_09.xml";
		//String pathXml = BASE_PATH_CASCADE + "cascade_quad_2018_06_10.xml";
		String pathXmlMatriculaLarga = BASE_PATH_CASCADE + "cascade_2018_06_15.xml";
		String pathXmlMatriculaCuadrada = BASE_PATH_CASCADE + "cascade_quad_2018_06_15.xml";
		
		//String pathImagenes = BASE_PATH + "test_fahr/";
		//String pathImagenes = BASE_PATH + "test_cuadrada/";
		String pathImagenes = BASE_PATH + "camion03_tren/";
		
		
		
		OpenCvWrapper openCvWrapper = new OpenCvWrapper(); 
		CascadeClassifier ccLarga = new CascadeClassifier(pathXmlMatriculaLarga);
		CascadeClassifier ccCuadrada = new CascadeClassifier(pathXmlMatriculaCuadrada);
		
		
		File[] folderFiles = new File(pathImagenes).listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File arg0, String arg1) {
				return (arg1.contains(".jpg") == true) && ((arg1.contains("_deteccion.jpg") == false));
				//return arg1.equalsIgnoreCase("20160928-152326.061.jpg");
			}
		});
		
		for(File f : folderFiles) {
		
			Mat image = Imgcodecs.imread(f.getAbsolutePath());
			Mat imagenResultado = Imgcodecs.imread(f.getAbsolutePath());
			
			/*
			if (f.getName().startsWith("z5_")) {
				image = openCvWrapper.rotar(image, -11);
			}
			*/
			
			image = openCvWrapper.rotar(image, 9);
			imagenResultado = openCvWrapper.rotar(imagenResultado, 9); 
			//imagenResultado = openCvWrapper.rotar(image, 2);
			//Imgcodecs.imwrite("rotada.jpg",image);
			
			System.out.println("Imagen: " + f.getAbsolutePath());
			
			// MATRICULAS LARGAS
			
			MatOfRect matriculasDetectadas = detectarMatriculas(ccLarga, image);
			
			if (matriculasDetectadas.toArray().length == 0) {
				System.out.println("No se han detectado matriculas largas!!!");
			} else {
					
				System.out.println("Se han detectado " + matriculasDetectadas.toArray().length + " matriculas largas");
				
				pintarMatriculas(matriculasDetectadas, imagenResultado, new Scalar(0, 0, 255));
			}
			
			// MATRICULAS CUADRADAS
			
			matriculasDetectadas = detectarMatriculas(ccCuadrada, image);
			if (matriculasDetectadas.toArray().length == 0) {
				System.out.println("No se han detectado matriculas cortas!!!");
			} else {
					
				System.out.println("Se han detectado " + matriculasDetectadas.toArray().length + " matriculas cortas");
				
				pintarMatriculas(matriculasDetectadas, imagenResultado, new Scalar(255, 0, 0));
			}
			
			Imgcodecs.imwrite(f.getAbsolutePath().replace(".jpg", "_deteccion.jpg"), imagenResultado);
			
			System.out.println("====");
		}
		
		System.out.println("==== FIN EJECUCION ====");

	}
	
	private static void pintarMatriculas(MatOfRect matriculasDetectadas, Mat image, Scalar color) {
		
		for (Rect rect : matriculasDetectadas.toArray()) {
            System.out.println("Coordenadas:");
            
            System.out.println("x=" + rect.x);
            System.out.println("y=" + rect.y);
            System.out.println("ancho=" + rect.width);
            System.out.println("alto=" + rect.height);
            
			Imgproc.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), color ,2);
        }
		
	}
	
	private static MatOfRect detectarMatriculas(CascadeClassifier cc, Mat image) {
		
		// https://stackoverflow.com/questions/20801015/recommended-values-for-opencv-detectmultiscale-parameters
		MatOfRect matriculasDetectadas = new MatOfRect();
		cc.detectMultiScale(image, matriculasDetectadas,1.05,10,0, new Size(80, 20), new Size(165,50));
		return matriculasDetectadas;
		
	}
	
}
