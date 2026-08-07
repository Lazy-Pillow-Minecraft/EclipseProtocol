package io.github.EclProtocol.util;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import io.github.EclProtocol.config.Constants;

public class TexturePackerRunner {

    public static void main(String[] args) {
        String inputDir = "assets/" + Constants.NAME_SPACE + "/textures/block";

        String outputDir = "assets/" + Constants.NAME_SPACE + "/textures/atlas";
        FileHandle outputDirHandle = new FileHandle(outputDir);
        if (outputDirHandle.exists()) {
            outputDirHandle.deleteDirectory(); // 删除整个目录
            System.out.println("🔥 已强制清理旧输出目录: " + outputDir);
        }
        outputDirHandle.mkdirs();

        TexturePacker.Settings settings = new TexturePacker.Settings();
        settings.pot = true;
        settings.paddingX = 2;
        settings.paddingY = 2;
        settings.duplicatePadding = true;
        settings.filterMin = Texture.TextureFilter.Nearest;
        settings.filterMag = Texture.TextureFilter.Nearest;
        settings.combineSubdirectories = true;

        TexturePacker.process(settings, inputDir, outputDir, "blocks.pack");

        System.out.println("✅ 图集打包完成！");
    }
}
