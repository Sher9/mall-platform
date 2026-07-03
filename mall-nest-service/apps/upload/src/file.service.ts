import { Injectable } from '@nestjs/common';
import { Express } from 'express';
import * as fs from 'fs';
import * as path from 'path';
import * as crypto from 'crypto';

@Injectable()
export class FileService {
  /**
   * 保存单个文件
   */
  async saveFile(file: Express.Multer.File, type: string): Promise<any> {
    // 创建日期目录
    const dateDir = new Date().toISOString().split('T')[0].replace(/-/g, '');
    const uploadDir = path.join(process.env.UPLOAD_DIR || './uploads', type, dateDir);
    
    if (!fs.existsSync(uploadDir)) {
      fs.mkdirSync(uploadDir, { recursive: true });
    }

    // 生成文件名
    const originalName = file.originalname;
    const extension = originalName.includes('.') ? originalName.substring(originalName.lastIndexOf('.')) : '';
    const newFileName = crypto.randomBytes(16).toString('hex') + extension;

    // 保存文件
    const filePath = path.join(uploadDir, newFileName);
    fs.writeFileSync(filePath, file.buffer);

    // 返回文件信息
    const accessUrl = `${process.env.FILE_ACCESS_URL || 'http://localhost:8000/files'}/${type}/${dateDir}/${newFileName}`;
    return {
      url: accessUrl,
      path: `${type}/${dateDir}/${newFileName}`,
      originalName,
      size: file.size,
    };
  }

  /**
   * 保存多个文件
   */
  async saveFiles(files: Express.Multer.File[], type: string): Promise<any[]> {
    const results = [];
    for (const file of files) {
      const result = await this.saveFile(file, type);
      results.push(result);
    }
    return results;
  }

  /**
   * 删除文件
   */
  async deleteFile(type: string, date: string, filename: string): Promise<boolean> {
    const filePath = path.join(process.env.UPLOAD_DIR || './uploads', type, date, filename);
    if (fs.existsSync(filePath)) {
      fs.unlinkSync(filePath);
      return true;
    }
    return false;
  }
}
