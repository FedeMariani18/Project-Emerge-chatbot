import os
import json
import pandas as pd
import matplotlib.pyplot as plt
import matplotlib.patches as mpatches
from matplotlib.lines import Line2D
from matplotlib.backends.backend_pdf import PdfPages


def load_validation_data(json_filename="./report/output_benchmark.json"):
    """Loads the JSON file and parses it into structured DataFrames."""
    if not os.path.exists(json_filename):
        print(f"Error: The file '{json_filename}' does not exist.")
        return None, None

    with open(json_filename, "r") as f:
        data = json.load(f)

    models_summary = []
    detailed_questions = []

    # Sort keys alphabetically to keep model families grouped together
    for model_name, info in sorted(data.items()):
        models_summary.append({
            "Model": model_name,
            "Total_Accuracy": info["totalAccuracy"] * 100,
            "Avg_Response_Time_Sec": info["avgResponseTime"] / 1000.0
        })

        for q in info["questionsResult"]:
            acc = q["accuracy"]

            if acc == 1.0:
                category = "Full Success (1.0)"
            elif acc == 0.0:
                category = "Total Failure (0.0)"
            else:
                category = "Partial Success (0.0 - 1.0)"

            detailed_questions.append({
                "Model": model_name,
                "Accuracy_Single": acc * 100,
                "Time_Sec": q["avgTime"] / 1000.0,
                "Outcome_Category": category
            })

    df_summary = pd.DataFrame(models_summary)
    df_detailed = pd.DataFrame(detailed_questions)

    return df_summary, df_detailed


def split_models(df):
    """Separates cloud models from local models."""
    cloud_mask = df["Model"].str.startswith(("gemini", "gpt"))

    cloud_models = df[cloud_mask].copy()
    local_models = df[~cloud_mask].copy()

    return cloud_models, local_models


def plot_accuracy_chart(pdf_worker, df_summary):
    """Page 1: Horizontal Bar Chart for Total Accuracy."""
    fig, ax = plt.subplots(figsize=(10, 6))

    bars = ax.barh(df_summary["Model"], df_summary["Total_Accuracy"], color='#2196F3', height=0.5)
    ax.invert_yaxis()

    for bar in bars:
        xval = bar.get_width()
        ax.text(
            xval + 1,
            bar.get_y() + bar.get_height() / 2.0,
            f"{xval:.1f}%",
            ha='left',
            va='center',
            fontweight='bold',
            fontsize=10
        )

    ax.set_title("Overall Model Accuracy in Tool Usage", fontsize=14, fontweight="bold", pad=15)
    ax.set_xlabel("Accuracy (%)", fontsize=11)
    ax.set_xlim(0, 110)
    ax.grid(axis="x", linestyle="--", alpha=0.5)

    plt.tight_layout()
    pdf_worker.savefig(fig)
    plt.close(fig)


def plot_response_time_boxplot_cloud(pdf_worker, df_detailed):
    """Page 2: Cloud Models Response Time Distribution."""
    fig, ax = plt.subplots(figsize=(10, 6))

    df_detailed.boxplot(
        column="Time_Sec",
        by="Model",
        ax=ax,
        vert=False,
        grid=False,
        patch_artist=True,
        boxprops=dict(facecolor="#e3f2fd", color="#2196F3"),
        flierprops=dict(marker='o', markerfacecolor='#FF5722', markeredgecolor='#FF5722'),
        medianprops=dict(color="#4CAF50", linewidth=2.5)
    )

    ax.invert_yaxis()
    ax.set_title("Cloud Models - Response Time Distribution", fontsize=14, fontweight="bold", pad=15)

    plt.suptitle("")

    ax.set_xlabel("Response Time (Seconds)", fontsize=11)
    ax.set_ylabel("Models", fontsize=11)
    ax.grid(axis="x", linestyle="--", alpha=0.5)

    box_patch = mpatches.Patch(
        facecolor='#e3f2fd',
        edgecolor='#2196F3',
        label='50% of Core Responses'
    )

    median_line = Line2D(
        [0],
        [0],
        color='w',
        marker='|',
        markeredgecolor='#4CAF50',
        markersize=14,
        markeredgewidth=3,
        label='Median (Central Value)'
    )

    whiskers_line = Line2D(
        [0],
        [0],
        color="#20659E",
        linestyle='-',
        marker='|',
        markersize=8,
        markeredgewidth=1.5,
        label='50% of Remaining Responses'
    )

    outlier_dot = Line2D(
        [0],
        [0],
        marker='o',
        color='w',
        markerfacecolor='#FF5722',
        markersize=8,
        label='Outliers (Unusually Long Queries)'
    )

    ax.legend(
        handles=[box_patch, median_line, whiskers_line, outlier_dot],
        loc='lower right',
        title="Boxplot Reading Guide",
        title_fontproperties={'weight': 'bold'},
        frameon=True,
        facecolor='white',
        edgecolor='gray'
    )

    plt.tight_layout()
    pdf_worker.savefig(fig)
    plt.close(fig)


def plot_response_time_boxplot_local(pdf_worker, df_detailed):
    """Page 3: Local Models Response Time Distribution."""
    fig, ax = plt.subplots(figsize=(10, 6))

    df_detailed.boxplot(
        column="Time_Sec",
        by="Model",
        ax=ax,
        vert=False,
        grid=False,
        patch_artist=True,
        boxprops=dict(facecolor="#fff3e0", color="#FB8C00"),
        flierprops=dict(marker='o', markerfacecolor='#D32F2F', markeredgecolor='#D32F2F'),
        medianprops=dict(color="#388E3C", linewidth=2.5)
    )

    ax.invert_yaxis()
    ax.set_title("Local Models - Response Time Distribution", fontsize=14, fontweight="bold", pad=15)

    plt.suptitle("")

    ax.set_xlabel("Response Time (Seconds)", fontsize=11)
    ax.set_ylabel("Models", fontsize=11)
    ax.grid(axis="x", linestyle="--", alpha=0.5)

    box_patch = mpatches.Patch(
        facecolor='#fff3e0',
        edgecolor='#FB8C00',
        label='50% of Core Responses'
    )

    median_line = Line2D(
        [0],
        [0],
        color='w',
        marker='|',
        markeredgecolor='#388E3C',
        markersize=14,
        markeredgewidth=3,
        label='Median (Central Value)'
    )

    whiskers_line = Line2D(
        [0],
        [0],
        color="#8D6E63",
        linestyle='-',
        marker='|',
        markersize=8,
        markeredgewidth=1.5,
        label='50% of Remaining Responses'
    )

    outlier_dot = Line2D(
        [0],
        [0],
        marker='o',
        color='w',
        markerfacecolor='#D32F2F',
        markersize=8,
        label='Outliers (Unusually Long Queries)'
    )

    ax.legend(
        handles=[box_patch, median_line, whiskers_line, outlier_dot],
        loc='lower right',
        title="Boxplot Reading Guide",
        title_fontproperties={'weight': 'bold'},
        frameon=True,
        facecolor='white',
        edgecolor='gray'
    )

    plt.tight_layout()
    pdf_worker.savefig(fig)
    plt.close(fig)


def plot_tradeoff_scatterplot_cloud(pdf_worker, df_summary):
    """Page 4: Cloud Models Trade-off Analysis."""
    fig, ax = plt.subplots(figsize=(11, 6))

    colors = plt.colormaps['tab10'].colors
    legend_elements = []

    ax.axhspan(80, 105, xmin=0, xmax=0.3, color='#e8f5e9', alpha=0.5, zorder=1)

    ax.text(
        df_summary['Avg_Response_Time_Sec'].min(),
        103,
        "OPTIMAL ZONE\n(Fast & Highly Accurate)",
        color='#2e7d32',
        fontsize=9,
        fontweight='bold',
        va='top'
    )

    for idx, row in df_summary.iterrows():
        color = colors[idx % len(colors)]
        model_number = str(idx + 1)

        ax.scatter(
            row['Avg_Response_Time_Sec'],
            row['Total_Accuracy'],
            s=250,
            color=color,
            alpha=0.9,
            edgecolors='black',
            zorder=3
        )

        ax.text(
            row['Avg_Response_Time_Sec'],
            row['Total_Accuracy'],
            model_number,
            fontsize=9,
            fontweight='bold',
            color='white',
            ha='center',
            va='center',
            zorder=4
        )

        legend_elements.append(
            Line2D(
                [0],
                [0],
                marker='o',
                color='w',
                markerfacecolor=color,
                markersize=10,
                markeredgecolor='black',
                label=f"[{model_number}] {row['Model']}"
            )
        )

    ax.set_title("Cloud Models - Accuracy vs Response Time", fontsize=14, fontweight="bold", pad=15)
    ax.set_xlabel("Average Response Time (Seconds)", fontsize=11)
    ax.set_ylabel("Total Accuracy (%)", fontsize=11)

    ax.set_ylim(-5, 110)

    max_time = df_summary['Avg_Response_Time_Sec'].max()
    ax.set_xlim(-max_time * 0.05, max_time * 1.1)

    ax.grid(True, linestyle="--", alpha=0.5, zorder=2)

    ax.legend(
        handles=legend_elements,
        loc='center left',
        bbox_to_anchor=(1.02, 0.5),
        title="Cloud Models",
        title_fontproperties={'weight': 'bold'},
        frameon=True
    )

    plt.tight_layout()
    pdf_worker.savefig(fig, bbox_inches='tight')
    plt.close(fig)


def plot_tradeoff_scatterplot_local(pdf_worker, df_summary):
    """Page 5: Local Models Trade-off Analysis."""
    fig, ax = plt.subplots(figsize=(11, 6))

    colors = plt.colormaps['tab10'].colors
    legend_elements = []

    ax.axhspan(80, 105, xmin=0, xmax=0.3, color='#fff8e1', alpha=0.5, zorder=1)

    ax.text(
        df_summary['Avg_Response_Time_Sec'].min(),
        103,
        "OPTIMAL ZONE\n(Fast & Highly Accurate)",
        color='#ef6c00',
        fontsize=9,
        fontweight='bold',
        va='top'
    )

    for idx, row in df_summary.iterrows():
        color = colors[idx % len(colors)]
        model_number = str(idx + 1)

        ax.scatter(
            row['Avg_Response_Time_Sec'],
            row['Total_Accuracy'],
            s=250,
            color=color,
            alpha=0.9,
            edgecolors='black',
            zorder=3
        )

        ax.text(
            row['Avg_Response_Time_Sec'],
            row['Total_Accuracy'],
            model_number,
            fontsize=9,
            fontweight='bold',
            color='white',
            ha='center',
            va='center',
            zorder=4
        )

        legend_elements.append(
            Line2D(
                [0],
                [0],
                marker='o',
                color='w',
                markerfacecolor=color,
                markersize=10,
                markeredgecolor='black',
                label=f"[{model_number}] {row['Model']}"
            )
        )

    ax.set_title("Local Models - Accuracy vs Response Time", fontsize=14, fontweight="bold", pad=15)
    ax.set_xlabel("Average Response Time (Seconds)", fontsize=11)
    ax.set_ylabel("Total Accuracy (%)", fontsize=11)

    ax.set_ylim(-5, 110)

    max_time = df_summary['Avg_Response_Time_Sec'].max()
    ax.set_xlim(-max_time * 0.05, max_time * 1.1)

    ax.grid(True, linestyle="--", alpha=0.5, zorder=2)

    ax.legend(
        handles=legend_elements,
        loc='center left',
        bbox_to_anchor=(1.02, 0.5),
        title="Local Models",
        title_fontproperties={'weight': 'bold'},
        frameon=True
    )

    plt.tight_layout()
    pdf_worker.savefig(fig, bbox_inches='tight')
    plt.close(fig)


def plot_pass_at_k_chart(pdf_worker, df_detailed):
    """Page 6: Pass@K analysis computed per-question and averaged per-model."""

    from math import comb

    fig, ax = plt.subplots(figsize=(11, 6))

    # Fixed number of executions per question
    n = 10

    # K values range
    k_values = list(range(1, n + 1))

    colors = plt.colormaps['tab10'].colors

    # Group by model
    grouped_models = df_detailed.groupby("Model")

    for idx, (model_name, model_df) in enumerate(grouped_models):

        model_pass_k_scores = []

        # Compute Pass@K for every K
        for k in k_values:

            question_pass_scores = []

            # Iterate over each question row
            for _, row in model_df.iterrows():

                # Recover successful executions from accuracy
                c = round((row["Accuracy_Single"] / 100.0) * n)

                # Standard Pass@K formula
                if n - c < k:
                    pass_k = 1.0
                else:
                    pass_k = 1 - (comb(n - c, k) / comb(n, k))

                question_pass_scores.append(pass_k)

            # Average Pass@K across all questions
            avg_pass_k = (sum(question_pass_scores) / len(question_pass_scores)) * 100
            model_pass_k_scores.append(avg_pass_k)

        color = colors[idx % len(colors)]

        ax.plot(
            k_values,
            model_pass_k_scores,
            marker='o',
            linewidth=2,
            label=model_name,
            color=color
        )

    ax.set_title(
        "Pass@K Analysis: Reliability Across Multiple Attempts",
        fontsize=14,
        fontweight="bold",
        pad=15
    )

    ax.set_xlabel("K Attempts", fontsize=11)
    ax.set_ylabel("Pass@K Probability (%)", fontsize=11)

    ax.set_xticks(k_values)
    ax.set_ylim(0, 105)

    ax.grid(True, linestyle="--", alpha=0.5)

    ax.legend(
        title="Models",
        title_fontproperties={'weight': 'bold'},
        loc='center left',
        bbox_to_anchor=(1.02, 0.5),
        frameon=True
    )

    plt.tight_layout()
    pdf_worker.savefig(fig, bbox_inches='tight')
    plt.close(fig)

def plot_accuracy_heatmap(pdf_worker, df_detailed):
    """Page 7: Heatmap showing per-question accuracy for each model."""

    fig, ax = plt.subplots(figsize=(12, 6))

    # Build matrix:
    # Rows -> Models
    # Columns -> Questions
    # Values -> Accuracy
    heatmap_data = (
        df_detailed
        .groupby("Model")["Accuracy_Single"]
        .apply(list)
        .apply(pd.Series)
    )

    # Normalize values between 0 and 1 for the colormap
    normalized_data = heatmap_data / 100.0

    # Custom 5-level color map:
    # 0.0   -> Dark Red
    # 0.25  -> Red/Orange
    # 0.5   -> Yellow
    # 0.75  -> Light Green
    # 1.0   -> Dark Green
    cmap = plt.colormaps["RdYlGn"]

    # Draw heatmap
    cax = ax.imshow(
        normalized_data,
        cmap=cmap,
        aspect='auto',
        vmin=0,
        vmax=1
    )

    # Axis labels
    ax.set_title(
        "Per-Question Accuracy Heatmap",
        fontsize=14,
        fontweight="bold",
        pad=15
    )

    ax.set_xlabel("Benchmark Questions", fontsize=11)
    ax.set_ylabel("Models", fontsize=11)

    # X-axis question labels
    question_labels = [f"Q{i+1}" for i in range(heatmap_data.shape[1])]
    ax.set_xticks(range(len(question_labels)))
    ax.set_xticklabels(question_labels)

    # Y-axis model labels
    ax.set_yticks(range(len(heatmap_data.index)))
    ax.set_yticklabels(heatmap_data.index)

    # Add accuracy values inside cells
    for i in range(normalized_data.shape[0]):
        for j in range(normalized_data.shape[1]):

            value = normalized_data.iloc[i, j]

            # Choose text color dynamically for readability
            text_color = "white" if value < 0.35 or value > 0.75 else "black"

            ax.text(
                j,
                i,
                f"{value:.1f}",
                ha='center',
                va='center',
                fontsize=8,
                fontweight='bold',
                color=text_color
            )

    # Add colorbar legend
    colorbar = fig.colorbar(cax)

    colorbar.set_label(
        "Accuracy Score",
        rotation=270,
        labelpad=15,
        fontsize=10
    )

    plt.tight_layout()
    pdf_worker.savefig(fig, bbox_inches='tight')
    plt.close(fig)

def main():
    json_filename = "./report/output_benchmark.json"
    pdf_filename = "./report/output_benchmark_chart.pdf"

    df_summary, df_detailed = load_validation_data(json_filename)

    if df_summary is None:
        return

    cloud_summary, local_summary = split_models(df_summary)
    cloud_detailed, local_detailed = split_models(df_detailed)

    try:
        print("Processing charts and generating multipage PDF...")

        with PdfPages(pdf_filename) as pdf:
            plot_accuracy_chart(pdf, df_summary)

            plot_accuracy_heatmap(pdf, df_detailed)

            plot_response_time_boxplot_cloud(pdf, cloud_detailed)
            plot_response_time_boxplot_local(pdf, local_detailed)

            plot_tradeoff_scatterplot_cloud(pdf, cloud_summary)
            plot_tradeoff_scatterplot_local(pdf, local_summary)

            plot_pass_at_k_chart(pdf, df_detailed)

        print(f"Execution finished! Complete report saved as: '{pdf_filename}'")

    except Exception as e:
        print(f"An error occurred during plot generations: {e}")


if __name__ == "__main__":
    main()