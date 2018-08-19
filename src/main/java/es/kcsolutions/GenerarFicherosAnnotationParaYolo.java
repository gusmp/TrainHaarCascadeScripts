package es.kcsolutions;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;

import boofcv.io.UtilIO;
import boofcv.io.image.UtilImageIO;

public class GenerarFicherosAnnotationParaYolo {

	public static void main(String[] args) throws Exception {
		
		//String sourceImagesFolder = "d:/maquinas_virtuales/sharedFolder/train_yolo/positive";
		String sourceImagesFolder = "d:/maquinas_virtuales/sharedFolder/train_yolo/positive_cuadradas";
		String annotationFolder = "d:/maquinas_virtuales/sharedFolder/train_yolo/annotations";
		
		File[] sourceFolderList = new File(sourceImagesFolder).listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File arg0, String arg1) {
				return arg1.contains(".jpg");
			}
		});
		
		for(File f:sourceFolderList) {
			
			BufferedImage image = UtilImageIO.loadImage(UtilIO.pathExample(f.getAbsolutePath()));
			//createAnnotationFile(f.getName(), annotationFolder, image.getHeight(), image.getWidth(), "matricula-larga");
			createAnnotationFile(f.getName(), annotationFolder, image.getHeight(), image.getWidth(), "matricula-cuadrada");
		}
		
		System.out.println("-- FIN --");

	}
	
	private static void createAnnotationFile(String fileName, String annotationFolder, int height, int width, String name) throws Exception 
	{
		
		String template = "<annotation>\n"+
				"<folder>images</folder>\n"+
				"<filename>" + fileName + "</filename>\n"+
				"<size>\n"+
				"<width>" + width + "</width>\n"+
				"<height>" + height + "</height>\n"+
				"<depth>3</depth>\n"+
				"</size>\n"+
				"<segmented>0</segmented>\n"+
				"<object>\n"+
				"<name>" + name + "</name>\n"+
				"<pose>Unspecified</pose>\n"+
				"<truncated>0</truncated>\n"+
				"<difficult>0</difficult>\n"+
				"<bndbox>\n"+
				"<xmin>0</xmin>\n"+
				"<ymin>0</ymin>\n"+
				"<xmax>" + width + "</xmax>\n"+
				"<ymax>" + height + "</ymax>\n"+
				"</bndbox>\n"+
				"</object>\n"+
				"</annotation>";
		FileOutputStream fout = new FileOutputStream(annotationFolder + "/" + fileName.replace("jpg", "xml"));
		fout.write(template.getBytes());
		fout.close();
		
	}

}
