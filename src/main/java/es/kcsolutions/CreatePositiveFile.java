package es.kcsolutions;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import boofcv.io.UtilIO;
import boofcv.io.image.UtilImageIO;

/**
 * Esta aplicacion sirve para generar el archivo positive.txt que contiene las matruulas recortadas, otros valores (1 0 0) y el ancho y alto
 * Con este fichero se ha de invocar al comando opencv_createsamples 
 *
 */
public class CreatePositiveFile {

	public static void main(String[] args) {
		
		//String sourcePositiveImagesFolder = "d:/maquinas_virtuales/sharedFolder/train_clasificador/positive/";
		//String targetPositiveFile = "d:/maquinas_virtuales/sharedFolder/train_clasificador/positive/positive.txt";
		
		String sourcePositiveImagesFolder = "d:/maquinas_virtuales/sharedFolder/train_clasificador/positive_cuadradas/";
		String targetPositiveFile = "d:/maquinas_virtuales/sharedFolder/train_clasificador/positive_cuadradas/positive.txt";
		
		File[] sourceFolderList = new File(sourcePositiveImagesFolder).listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File arg0, String arg1) {
				return arg1.contains(".jpg");
			}
		});
		
		System.out.println("Directorio origen: " + sourcePositiveImagesFolder);
		System.out.println("Archivo positivos: " + targetPositiveFile);
		
		int counter = 0;
		float w = 0;
		float h = 0;

		try {
			
			FileOutputStream targetPositiveFileStream = new FileOutputStream(targetPositiveFile);
			String positiveFileLine = "";
			for(File f : sourceFolderList) {
				System.out.println("Procesando..." + f.getName());
				
				BufferedImage image = UtilImageIO.loadImage(UtilIO.pathExample(f.getAbsolutePath()));

				positiveFileLine = "";
				positiveFileLine += f.getName() + " 1 0 0 " + image.getWidth() + " " + image.getHeight() + "\n";
				
				w = (w + image.getWidth())/2;
				h = (h + image.getHeight())/2;
				
				targetPositiveFileStream.write(positiveFileLine.getBytes());
				
				counter++;
				System.out.println("OK");
				
			}
			
			targetPositiveFileStream.close();
			
			System.out.println("Fin creacion archivo positivos");
			System.out.println("Total recortes: " + counter);
			
			System.out.println("Promedio width : " + w);
			System.out.println("Promedio height: " + h);
			
			System.out.println("Recuerda generar el fichero vec!!");
			
			System.out.println("d:\\maquinas_virtuales\\sharedFolder\\train_clasificador\\opencv\\opencv_createsamples.exe -vec "
					+ "d:\\maquinas_virtuales\\sharedFolder\\train_clasificador\\positive\\vecfile.vec -w 52 -h 13 "
					+ "-info d:\\maquinas_virtuales\\sharedFolder\\train_clasificador\\positive\\positive.txt -num NUMERO_IMG*0.9 (ver excel)");
			
			System.out.println("Ver http://answers.opencv.org/question/84852/traincascades-error-required-leaf-false-alarm-rate-achieved-branch-training-terminated/");
			System.out.println("Si entremo muestra: Required leaf false alarm rate achieved. Branch training terminated.");
			
			
		} catch(Exception exc) {
			System.out.println("Error: " + exc.getMessage());
			System.out.println("Detalle: " + exc.toString());
		}
	}
}
