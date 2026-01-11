"use client";

import React from "react";
// import LineChartOne from "@/components/Charts/line/LineChartOne";

export default function DashboardPage() {
  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div>
        <h1 className="text-3xl font-bold text-gray-900 dark:text-white">
          Dashboard
        </h1>
        <p className="mt-2 text-sm text-gray-600 dark:text-gray-400">
          Financial and Securities Back Office Overview
        </p>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-4">
        <StatsCard
          title="Total Assets"
          value="₩ 125.5B"
          change="+12.5%"
          isPositive={true}
        />
        <StatsCard
          title="Total Portfolios"
          value="1,234"
          change="+5.2%"
          isPositive={true}
        />
        <StatsCard
          title="Active Orders"
          value="89"
          change="-2.3%"
          isPositive={false}
        />
        <StatsCard
          title="Clients"
          value="456"
          change="+8.1%"
          isPositive={true}
        />
      </div>

      {/* Charts */}
      <div className="grid grid-cols-1 gap-4 lg:grid-cols-2">
        <div className="rounded-2xl border border-gray-200 bg-white p-6 dark:border-gray-800 dark:bg-gray-900">
          <h3 className="mb-4 text-xl font-semibold text-gray-900 dark:text-white">
            Portfolio Performance
          </h3>
          <div className="h-[310px] flex items-center justify-center text-gray-500">
            Chart will be displayed here
          </div>
        </div>

        <div className="rounded-2xl border border-gray-200 bg-white p-6 dark:border-gray-800 dark:bg-gray-900">
          <h3 className="mb-4 text-xl font-semibold text-gray-900 dark:text-white">
            Recent Activities
          </h3>
          <div className="space-y-3">
            <ActivityItem
              title="Order Executed"
              description="Buy 100 shares of AAPL"
              time="5 minutes ago"
            />
            <ActivityItem
              title="New Client"
              description="John Doe registered"
              time="1 hour ago"
            />
            <ActivityItem
              title="Portfolio Updated"
              description="Rebalanced Tech Portfolio"
              time="2 hours ago"
            />
            <ActivityItem
              title="KYC Approved"
              description="Jane Smith verification completed"
              time="3 hours ago"
            />
          </div>
        </div>
      </div>

      {/* Recent Transactions Table */}
      <div className="rounded-2xl border border-gray-200 bg-white p-6 dark:border-gray-800 dark:bg-gray-900">
        <h3 className="mb-4 text-xl font-semibold text-gray-900 dark:text-white">
          Recent Transactions
        </h3>
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead>
              <tr className="border-b border-gray-200 dark:border-gray-800">
                <th className="pb-3 text-left text-sm font-medium text-gray-600 dark:text-gray-400">
                  Client
                </th>
                <th className="pb-3 text-left text-sm font-medium text-gray-600 dark:text-gray-400">
                  Asset
                </th>
                <th className="pb-3 text-left text-sm font-medium text-gray-600 dark:text-gray-400">
                  Type
                </th>
                <th className="pb-3 text-right text-sm font-medium text-gray-600 dark:text-gray-400">
                  Amount
                </th>
                <th className="pb-3 text-right text-sm font-medium text-gray-600 dark:text-gray-400">
                  Status
                </th>
              </tr>
            </thead>
            <tbody>
              <TransactionRow
                client="John Doe"
                asset="AAPL"
                type="Buy"
                amount="₩ 5,000,000"
                status="Completed"
              />
              <TransactionRow
                client="Jane Smith"
                asset="TSLA"
                type="Sell"
                amount="₩ 3,500,000"
                status="Completed"
              />
              <TransactionRow
                client="Mike Johnson"
                asset="MSFT"
                type="Buy"
                amount="₩ 8,200,000"
                status="Pending"
              />
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}

// Stats Card Component
function StatsCard({
  title,
  value,
  change,
  isPositive,
}: {
  title: string;
  value: string;
  change: string;
  isPositive: boolean;
}) {
  return (
    <div className="rounded-2xl border border-gray-200 bg-white p-6 dark:border-gray-800 dark:bg-gray-900">
      <p className="text-sm text-gray-600 dark:text-gray-400">{title}</p>
      <h3 className="mt-2 text-2xl font-bold text-gray-900 dark:text-white">
        {value}
      </h3>
      <p
        className={`mt-2 text-sm ${
          isPositive
            ? "text-success-600 dark:text-success-400"
            : "text-red-600 dark:text-red-400"
        }`}
      >
        {change} from last month
      </p>
    </div>
  );
}

// Activity Item Component
function ActivityItem({
  title,
  description,
  time,
}: {
  title: string;
  description: string;
  time: string;
}) {
  return (
    <div className="flex items-start space-x-3">
      <div className="flex h-10 w-10 flex-shrink-0 items-center justify-center rounded-full bg-brand-50 dark:bg-brand-950">
        <svg
          className="h-5 w-5 text-brand-600 dark:text-brand-400"
          fill="none"
          strokeLinecap="round"
          strokeLinejoin="round"
          strokeWidth="2"
          viewBox="0 0 24 24"
          stroke="currentColor"
        >
          <path d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path>
        </svg>
      </div>
      <div className="flex-1 min-w-0">
        <p className="text-sm font-medium text-gray-900 dark:text-white">
          {title}
        </p>
        <p className="text-sm text-gray-600 dark:text-gray-400">{description}</p>
        <p className="mt-1 text-xs text-gray-500 dark:text-gray-500">{time}</p>
      </div>
    </div>
  );
}

// Transaction Row Component
function TransactionRow({
  client,
  asset,
  type,
  amount,
  status,
}: {
  client: string;
  asset: string;
  type: string;
  amount: string;
  status: string;
}) {
  return (
    <tr className="border-b border-gray-100 dark:border-gray-800 last:border-b-0">
      <td className="py-4 text-sm text-gray-900 dark:text-white">{client}</td>
      <td className="py-4 text-sm text-gray-900 dark:text-white">{asset}</td>
      <td className="py-4 text-sm">
        <span
          className={`inline-flex rounded-full px-2 py-1 text-xs font-medium ${
            type === "Buy"
              ? "bg-success-50 text-success-700 dark:bg-success-950 dark:text-success-400"
              : "bg-orange-50 text-orange-700 dark:bg-orange-950 dark:text-orange-400"
          }`}
        >
          {type}
        </span>
      </td>
      <td className="py-4 text-right text-sm text-gray-900 dark:text-white">
        {amount}
      </td>
      <td className="py-4 text-right text-sm">
        <span
          className={`inline-flex rounded-full px-2 py-1 text-xs font-medium ${
            status === "Completed"
              ? "bg-success-50 text-success-700 dark:bg-success-950 dark:text-success-400"
              : "bg-gray-100 text-gray-700 dark:bg-gray-800 dark:text-gray-400"
          }`}
        >
          {status}
        </span>
      </td>
    </tr>
  );
}
