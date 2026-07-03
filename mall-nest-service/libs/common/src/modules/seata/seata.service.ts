import { Injectable, OnModuleInit, Inject } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import * as axios from 'axios';

@Injectable()
export class SeataService implements OnModuleInit {
  private txId: string | null = null;
  private serviceName: string;

  constructor(
    private configService: ConfigService,
    @Inject('SEATA_SERVICE_NAME') private readonly serviceNameInject: string,
  ) {
    this.serviceName = serviceNameInject;
  }

  async onModuleInit() {
    console.log(`✅ Seata 分布式事务模块已初始化: ${this.serviceName}`);
  }

  /**
   * 开启全局事务
   */
  async beginGlobalTransaction(): Promise<string> {
    try {
      const seataServer = this.configService.get<string>('SEATA_SERVER', 'localhost:8091');
      
      // 模拟调用 Seata Server 开启全局事务
      // 实际项目中需要使用 Seata 的 TM（事务管理器）API
      this.txId = `TX_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
      
      console.log(`✅ 开启全局事务: ${this.txId}`);
      return this.txId;
    } catch (error) {
      console.error('❌ 开启全局事务失败:', error.message);
      throw error;
    }
  }

  /**
   * 注册分支事务
   */
  async registerBranchTransaction(txId: string, resourceId: string): Promise<void> {
    try {
      console.log(`✅ 注册分支事务: ${txId}@${this.serviceName}#${resourceId}`);
      
      // 模拟向 Seata Server 注册分支事务
      // 实际项目中需要使用 Seata 的 RM（资源管理器）API
    } catch (error) {
      console.error('❌ 注册分支事务失败:', error.message);
      throw error;
    }
  }

  /**
   * 提交全局事务
   */
  async commitGlobalTransaction(txId: string): Promise<void> {
    try {
      console.log(`✅ 提交全局事务: ${txId}`);
      
      // 模拟调用 Seata Server 提交全局事务
      // 实际项目中需要使用 Seata 的 TM API
    } catch (error) {
      console.error('❌ 提交全局事务失败:', error.message);
      throw error;
    }
  }

  /**
   * 回滚全局事务
   */
  async rollbackGlobalTransaction(txId: string): Promise<void> {
    try {
      console.log(`✅ 回滚全局事务: ${txId}`);
      
      // 模拟调用 Seata Server 回滚全局事务
      // 实际项目中需要使用 Seata 的 TM API
    } catch (error) {
      console.error('❌ 回滚全局事务失败:', error.message);
      throw error;
    }
  }

  /**
   * 执行分布式事务
   * @param callback 业务逻辑回调
   */
  async executeInTransaction<T>(callback: (txId: string) => Promise<T>): Promise<T> {
    let txId: string | null = null;
    
    try {
      // 开启全局事务
      txId = await this.beginGlobalTransaction();
      
      // 执行业务逻辑
      const result = await callback(txId);
      
      // 提交全局事务
      await this.commitGlobalTransaction(txId);
      
      return result;
    } catch (error) {
      // 回滚全局事务
      if (txId) {
        await this.rollbackGlobalTransaction(txId);
      }
      throw error;
    }
  }

  /**
   * 获取当前事务 ID
   */
  getCurrentTxId(): string | null {
    return this.txId;
  }
}
