import { Controller, Post, Get, Delete, UploadedFile, UploadedFiles, Param, Query, Res } from '@nestjs/common';
import { FileInterceptor, FilesInterceptor } from '@nestjs/platform-express';
import { FileService } from './file.service';
import { Response } from 'express';
import * as path from 'path';
import * as fs from 'fs';

@Controller('api/upload')
export class FileController {
  constructor(private fileService: FileService) {}

  /**
   * 单个文件上传
   */
  @Post('single')
  @UseInterceptors(FileInterceptor('file'))
  async uploadSingle(@UploadedFile() file: Express.Multer.File, @Query('type') type: string = 'image') {
    const result = await this.fileService.saveFile(file, type);
    return {
      code: 200,
      message: '上传成功',
      data: result,
    };
  }

  /**
   * 多个文件上传
   */
  @Post('multiple')
  @UseInterceptors(FilesInterceptor('files'))
  async uploadMultiple(@UploadedFiles() files: Express.Multer.File[], @Query('type') type: string = 'image') {
    const results = await this.fileService.saveFiles(files, type);
    return {
      code: 200,
      message: '上传成功',
      data: results,
    };
  }

  /**
   * 删除文件
   */
  @Delete(':type/:date/:filename')
  async deleteFile(
    @Param('type') type: string,
    @Param('date') date: string,
    @Param('filename') filename: string,
  ) {
    const result = await this.fileService.deleteFile(type, date, filename);
    return {
      code: 200,
      message: result ? '删除成功' : '文件不存在',
      data: null,
    };
  }

  /**
   * 访问上传的文件
   */
  @Get('files/:type/:date/:filename')
  getFile(
    @Param('type') type: string,
    @Param('date') date: string,
    @Param('filename') filename: string,
    @Res() res: Response,
  ) {
    const filePath = path.join(process.env.UPLOAD_DIR || './uploads', type, date, filename);
    if (fs.existsSync(filePath)) {
      res.sendFile(filePath);
    } else {
      res.status(404).json({ code: 404, message: '文件不存在', data: null });
    }
  }
}
